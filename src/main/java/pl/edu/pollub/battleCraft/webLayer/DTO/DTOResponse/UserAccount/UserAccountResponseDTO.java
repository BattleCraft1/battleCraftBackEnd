package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Play;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountResponseDTO {

    public UserAccountResponseDTO(UserAccount userAccount){
        this.name = userAccount.getName();
        this.nameChange = userAccount.getName();
        this.email = userAccount.getEmail();
        this.firstname = userAccount.getFirstname();
        this.lastname = userAccount.getLastname();
        this.phoneNumber = userAccount.getPhoneNumber();

        if(userAccount instanceof Player){
            Player player = (Player) userAccount;
            this.points = player.getBattles().stream().mapToInt(Play::getPoints).sum();
            this.numberOfBattles = player.getBattles().size();
            this.participatedTournaments = player.getParticipatedTournaments().stream()
                    .map(participation -> {
                        Tournament tournament = participation.getParticipatedTournament();
                        return new InvitationDTO(tournament.getName(),participation.isAccepted());
                    })
                    .collect(Collectors.toList());
        }

        if(userAccount instanceof Organizer){
            Organizer organizer = (Organizer) userAccount;
            this.createdGames = organizer.getCreatedGames().stream().map(Game::getName).collect(Collectors.toList());
            this.organizedTournaments = organizer.getOrganizedTournaments().stream()
                    .map(organization -> {
                        Tournament tournament = organization.getOrganizedTournament();
                        return new InvitationDTO(tournament.getName(),organization.isAccepted());
                    })
                    .collect(Collectors.toList());
        }
    }

    public String nameChange;
    public String name;
    public String email;
    public String firstname;
    public String lastname;
    public String phoneNumber;
    public String status;
    public int points;
    public int numberOfBattles;
    public List<InvitationDTO> participatedTournaments;
    public List<InvitationDTO> organizedTournaments;
    public List<String> createdGames;
}
