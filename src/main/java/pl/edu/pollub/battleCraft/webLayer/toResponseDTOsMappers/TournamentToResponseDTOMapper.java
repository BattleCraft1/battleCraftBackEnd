package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TournamentToResponseDTOMapper {

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public TournamentToResponseDTOMapper(AuthorityRecognizer authorityRecognizer) {
        this.authorityRecognizer = authorityRecognizer;
    }

    public TournamentResponseDTO map(Tournament tournament){
        TournamentResponseDTO tournamentResponseDTO = new TournamentResponseDTO();
        tournamentResponseDTO.setOrganizers(tournament.getOrganizations().stream()
                .map(organization -> {
                    Organizer organizer = organization.getOrganizer();
                    return new InvitationResponseDTO(organizer.getName(),organization.isAccepted());
                })
                .collect(Collectors.toList()));

        if(tournament.getTournamentType()== TournamentType.DUEL){
            tournamentResponseDTO.setParticipants(createParticipantsForDuel(tournament.getParticipation()));
        }
        else{
            tournamentResponseDTO.setParticipants(createParticipantsForGroup(tournament.getParticipation()));
        }

        tournamentResponseDTO.setName(tournament.getName());
        tournamentResponseDTO.setNameChange(tournament.getName());
        tournamentResponseDTO.setToursCount(tournament.getTurnsCount());
        tournamentResponseDTO.setTablesCount(tournament.getTablesCount());
        tournamentResponseDTO.setPlayersOnTableCount(tournament.getPlayersOnTableCount());
        tournamentResponseDTO.setGame(tournament.getGame().getName());
        tournamentResponseDTO.setDateOfStart(tournament.getDateOfStart());
        tournamentResponseDTO.setDateOfEnd(tournament.getDateOfEnd());
        tournamentResponseDTO.setProvince(tournament.getAddressOwnership().getAddress().getProvince().name());
        tournamentResponseDTO.setCity(tournament.getAddressOwnership().getAddress().getCity());
        tournamentResponseDTO.setStreet(tournament.getAddressOwnership().getAddress().getStreet());
        tournamentResponseDTO.setZipCode(tournament.getAddressOwnership().getAddress().getZipCode());
        tournamentResponseDTO.setDescription(tournament.getAddressOwnership().getAddress().getDescription());
        if(tournament.isBanned()) {
            tournamentResponseDTO.setStatus("BANNED");
            tournamentResponseDTO.setCanCurrentUserEdit(authorityRecognizer.getCurrentUserRoleFromContext().equals("ROLE_ADMIN"));
        }
        else {
            tournamentResponseDTO.setStatus(tournament.getStatus().name());
            boolean currentUserCanEdit = tournament.getOrganizations().stream()
                    .map(organization -> organization.getOrganizer().getName())
                    .collect(Collectors.toList()).contains(authorityRecognizer.getCurrentUserNameFromContext())
                    || authorityRecognizer.getCurrentUserRoleFromContext().equals("ROLE_ADMIN");
            tournamentResponseDTO.setCanCurrentUserEdit(currentUserCanEdit);
        }
        return tournamentResponseDTO;
    }

    private List<List<InvitationResponseDTO>> createParticipantsForDuel(List<Participation> participation){
        return participation.stream()
                .map(participationElement -> {
                    Player player = participationElement.getPlayer();
                    return Collections.singletonList(new InvitationResponseDTO(player.getName(),participationElement.isAccepted()));
                })
                .collect(Collectors.toList());
    }

    private List<List<InvitationResponseDTO>> createParticipantsForGroup(List<Participation> participation){
        List<List<InvitationResponseDTO>> playersGroups = new ArrayList<>();
        List<String> includedPlayersNames = new ArrayList<>();

        for(Participation participationElement:participation){
            String playerName = participationElement.getPlayer().getName();
            if(includedPlayersNames.contains(playerName))
                continue;

            List<InvitationResponseDTO> playersGroup = new ArrayList<>();
            playersGroup.add(new InvitationResponseDTO(playerName,participationElement.isAccepted()));
            includedPlayersNames.add(playerName);

            participation.stream().filter(participationElement2 ->
                    participationElement2.getGroupNumber().equals(participationElement.getGroupNumber()) &&
                    !includedPlayersNames.contains(participationElement2.getPlayer().getName()))
                    .findFirst()
                    .ifPresent(participationElement2 ->{
                        String player2Name = participationElement2.getPlayer().getName();
                        playersGroup.add(new InvitationResponseDTO(player2Name,participationElement2.isAccepted()));
                        includedPlayersNames.add(player2Name);
                    });

            playersGroups.add(playersGroup);
        }

        return playersGroups;
    }
}
