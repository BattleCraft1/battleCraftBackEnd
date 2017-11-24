package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.group.ParticipationGroup;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;

import java.util.*;
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
        tournamentResponseDTO.setToursCount(tournament.getToursCount());
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

    private List<List<InvitationResponseDTO>> createParticipantsForDuel(Set<Participation> participation){
        return participation.stream()
                .map(participationElement -> {
                    Player player = participationElement.getPlayer();
                    return Collections.singletonList(new InvitationResponseDTO(player.getName(),participationElement.isAccepted()));
                })
                .collect(Collectors.toList());
    }

    private List<List<InvitationResponseDTO>> createParticipantsForGroup(Set<Participation> participation){
        Set<ParticipationGroup> groups = participation.stream().map(Participation::getParticipationGroup).collect(Collectors.toSet());

        return groups.stream().map(group -> {
            List<InvitationResponseDTO> participants = new ArrayList<>();

            group.getParticipationInGroup().forEach(
                    participation1 -> participants.add(new InvitationResponseDTO(participation1.getPlayer().getName(),participation1.isAccepted())));

            return participants;}
        ).collect(Collectors.toList());
    }
}
