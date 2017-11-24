package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.nullObjectPattern.NullParticipation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.PlayerFinishedInvitationResponse;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.PlayerGroupFinishedInvitationResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.PlayerInvitationResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

import java.util.Optional;
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
        userAccountResponseDTO.setPhoneNumber(Optional.ofNullable(userAccount.getPhoneNumber()).orElse(""));
        userAccountResponseDTO.setProvince(userAccount.getAddress().getProvince().name());
        userAccountResponseDTO.setCity(userAccount.getAddress().getCity());
        userAccountResponseDTO.setStreet(userAccount.getAddress().getStreet());
        userAccountResponseDTO.setZipCode(userAccount.getAddress().getZipCode());
        userAccountResponseDTO.setDescription(Optional.ofNullable(userAccount.getAddress().getDescription()).orElse(""));
        userAccountResponseDTO.setStatus(userAccount.getStatus().name());

        if(userAccount instanceof Player){
            Player player = (Player) userAccount;
            userAccountResponseDTO.setBanned(player.isBanned());
            userAccountResponseDTO.setPoints(player.getBattles().stream().mapToInt(Play::getPoints).sum());
            userAccountResponseDTO.setNumberOfBattles(player.getBattles().size());
            this.createParticipation(userAccountResponseDTO,player);
        }

        if(userAccount instanceof Organizer){
            Organizer organizer = (Organizer) userAccount;
            userAccountResponseDTO.setCreatedGames(organizer.getCreatedGames().stream().map(Game::getName).collect(Collectors.toList()));
            this.createOrganizations(userAccountResponseDTO,organizer);
        }
        return userAccountResponseDTO;
    }

    private void createParticipation(UserAccountResponseDTO userAccountResponseDTO, Player player){
        player.getParticipation().forEach(participation -> {
                    Tournament tournament = participation.getParticipatedTournament();
                    if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                            tournament.getStatus() != TournamentStatus.FINISHED){
                        if(tournament.getTournamentType() == TournamentType.GROUP){
                            Participation secondPlayerParticipation = participation.getParticipationGroup()
                                    .getParticipationInGroup().stream().filter(participation1 -> !participation1.getPlayer().equals(player))
                                    .findFirst().orElseThrow(() -> new ObjectNotFoundException(Player.class));
                            userAccountResponseDTO.getParticipatedTournaments()
                                    .add(new PlayerInvitationResponseDTO(secondPlayerParticipation.getPlayer().getName(),
                                            secondPlayerParticipation.isAccepted(),
                                            tournament.getName(),participation.isAccepted()));
                        }
                        else{
                            userAccountResponseDTO.getParticipatedTournaments()
                                    .add(new InvitationResponseDTO(tournament.getName(),participation.isAccepted()));
                        }
                    }
                    else{
                        if(tournament.getTournamentType() == TournamentType.GROUP){
                            Participation secondPlayerParticipation = participation.getParticipationGroup()
                                    .getParticipationInGroup().stream().filter(participation1 -> !participation1.getPlayer().equals(player))
                                    .findFirst().orElseThrow(() -> new ObjectNotFoundException(Player.class));
                            userAccountResponseDTO.getFinishedParticipatedTournaments().add(
                                    new PlayerGroupFinishedInvitationResponseDTO(tournament.getName(),secondPlayerParticipation.getPlayer().getName())
                            );
                        }
                        else{
                            userAccountResponseDTO.getFinishedParticipatedTournaments().add(
                                    new PlayerFinishedInvitationResponse(tournament.getName())
                            );
                        }
                    }
                });
    }

    private void createOrganizations(UserAccountResponseDTO userAccountResponseDTO, Organizer organizer){
        organizer.getOrganizations()
                .forEach(organization -> {
                    Tournament tournament = organization.getOrganizedTournament();
                    if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                            tournament.getStatus() != TournamentStatus.FINISHED)
                        userAccountResponseDTO.getOrganizedTournaments()
                                .add(new InvitationResponseDTO(tournament.getName(),organization.isAccepted()));
                    else
                        userAccountResponseDTO.getFinishedOrganizedTournaments().add(tournament.getName());
                });
    }
}
