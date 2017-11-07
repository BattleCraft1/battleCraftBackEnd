package pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

import java.util.stream.Collectors;

@Component
public class TournamentToResponseDTOMapper {
    public TournamentResponseDTO map(Tournament tournament){
        TournamentResponseDTO tournamentResponseDTO = new TournamentResponseDTO();
        tournamentResponseDTO.setOrganizers(tournament.getOrganizations().stream()
                .map(organization -> {
                    Organizer organizer = organization.getOrganizer();
                    return new InvitationDTO(organizer.getName(),organization.isAccepted());
                })
                .collect(Collectors.toList()));

        tournamentResponseDTO.setParticipants(tournament.getParticipation().stream()
                .map(participation -> {
                    Player player = participation.getPlayer();
                    return new InvitationDTO(player.getName(),participation.isAccepted());
                })
                .collect(Collectors.toList()));

        tournamentResponseDTO.setName(tournament.getName());
        tournamentResponseDTO.setNameChange(tournament.getName());
        tournamentResponseDTO.setTablesCount(tournament.getTablesCount());
        tournamentResponseDTO.setTablesCount(tournament.getToursCount());
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
}
