package pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

import java.util.stream.Collectors;

@Component
public class UserAccountToResponseDTOMapper {
    public UserAccountResponseDTO map(UserAccount userAccount){
        UserAccountResponseDTO userAccountResponseDTO = new UserAccountResponseDTO();
        userAccountResponseDTO.setName(userAccount.getName());
        userAccountResponseDTO.setNameChange(userAccount.getName());
        userAccountResponseDTO.setEmail(userAccount.getEmail());
        userAccountResponseDTO.setFirstname(userAccount.getFirstname());
        userAccountResponseDTO.setLastname(userAccount.getLastname());
        userAccountResponseDTO.setPhoneNumber(userAccount.getPhoneNumber());
        userAccountResponseDTO.setProvince(userAccount.getAddress().getProvince().name());
        userAccountResponseDTO.setCity(userAccount.getAddress().getCity());
        userAccountResponseDTO.setStreet(userAccount.getAddress().getStreet());
        userAccountResponseDTO.setZipCode(userAccount.getAddress().getZipCode());
        userAccountResponseDTO.setDescription(userAccount.getAddress().getDescription());
        userAccountResponseDTO.setStatus(userAccount.getStatus().name());

        if(userAccount instanceof Player){
            Player player = (Player) userAccount;
            userAccountResponseDTO.setBanned(player.isBanned());
            userAccountResponseDTO.setPoints(player.getBattles().stream().mapToInt(Play::getPoints).sum());
            userAccountResponseDTO.setNumberOfBattles(player.getBattles().size());
            player.getParticipatedTournaments()
                    .forEach(participation -> {
                        Tournament tournament = participation.getParticipatedTournament();
                        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                                tournament.getStatus() != TournamentStatus.FINISHED)
                            userAccountResponseDTO.getParticipatedTournaments()
                                    .add(new InvitationDTO(tournament.getName(),participation.isAccepted()));
                        else
                            userAccountResponseDTO.getFinishedParticipatedTournaments().add(tournament.getName());
                    });
        }

        if(userAccount instanceof Organizer){
            Organizer organizer = (Organizer) userAccount;
            userAccountResponseDTO.setCreatedGames(organizer.getCreatedGames().stream().map(Game::getName).collect(Collectors.toList()));
                    ;
            organizer.getOrganizations()
                    .forEach(organization -> {
                        Tournament tournament = organization.getOrganizedTournament();
                        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                                tournament.getStatus() != TournamentStatus.FINISHED)
                            userAccountResponseDTO.getOrganizedTournaments()
                                    .add(new InvitationDTO(tournament.getName(),organization.isAccepted()));
                        else
                            userAccountResponseDTO.getFinishedOrganizedTournaments().add(tournament.getName());
                    });
        }
        return userAccountResponseDTO;
    }
}
