package pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TournamentToResponseDTOMapper {
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
        tournamentResponseDTO.setTablesCount(tournament.getToursCount());
        tournamentResponseDTO.setTablesCount(tournament.getTablesCount());
        tournamentResponseDTO.setPlayersOnTableCount(tournament.getPlayersOnTableCount());
        tournamentResponseDTO.setGame(tournament.getGame().getName());
        tournamentResponseDTO.setDateOfStart(tournament.getDateOfStart());
        tournamentResponseDTO.setDateOfEnd(tournament.getDateOfEnd());
        tournamentResponseDTO.setProvince(tournament.getAddress().getProvince().name());
        tournamentResponseDTO.setCity(tournament.getAddress().getCity());
        tournamentResponseDTO.setStreet(tournament.getAddress().getStreet());
        tournamentResponseDTO.setZipCode(tournament.getAddress().getZipCode());
        tournamentResponseDTO.setDescription(tournament.getAddress().getDescription());
        if(tournament.isBanned())
            tournamentResponseDTO.setStatus("BANNED");
        else
            tournamentResponseDTO.setStatus(tournament.getStatus().name());
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
