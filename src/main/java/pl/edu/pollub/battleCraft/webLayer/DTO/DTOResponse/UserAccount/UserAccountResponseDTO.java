package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Participation.FinishedParticipatedTournamentResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Participation.FinishedParticipatedGroupTournamentResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.GroupInvitationResponseDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class UserAccountResponseDTO {
    private String nameChange;
    private String name;
    private String email;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String status;
    private String province;
    private String city;
    private String street;
    private String zipCode;
    private String description;
    private int points;
    private int numberOfBattles;
    private List<InvitationResponseDTO> participatedTournaments = new ArrayList<>();
    private List<FinishedParticipatedTournamentResponseDTO> finishedParticipatedTournaments = new ArrayList<>();
    private List<InvitationResponseDTO> organizedTournaments = new ArrayList<>();
    private List<String> finishedOrganizedTournaments = new ArrayList<>();
    private List<String> createdGames = new ArrayList<>();
    private boolean banned;
    private boolean canCurrentUserEdit;
    private String newToken;

    public void addParticipatedGroupTournament(Tournament tournament, Participation firstPlayerParticipation, Participation secondPlayerParticipation){
        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                tournament.getStatus() != TournamentStatus.FINISHED){
            this.participatedTournaments.add(new GroupInvitationResponseDTO(
                    tournament.getName(),
                    secondPlayerParticipation.getPlayer().getName(),
                    secondPlayerParticipation.isAccepted(),
                    firstPlayerParticipation.isAccepted())
            );
        }
        else{
            this.finishedParticipatedTournaments.add(
                    new FinishedParticipatedGroupTournamentResponseDTO(tournament.getName(),secondPlayerParticipation.getPlayer().getName())
            );
        }
    }

    public void addParticipatedDuelTournament(Tournament tournament, Participation playerParticipation){
        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                tournament.getStatus() != TournamentStatus.FINISHED){
            this.participatedTournaments.add(new InvitationResponseDTO(tournament.getName(),playerParticipation.isAccepted()));
        }
        else{
            this.finishedParticipatedTournaments.add(new FinishedParticipatedTournamentResponseDTO(tournament.getName()));
        }
    }

    public void addOrganizedTournament(Organization organization){
        Tournament tournament = organization.getOrganizedTournament();
        if(tournament.getStatus() != TournamentStatus.IN_PROGRESS &&
                tournament.getStatus() != TournamentStatus.FINISHED)
            this.getOrganizedTournaments()
                    .add(new InvitationResponseDTO(tournament.getName(),organization.isAccepted()));
        else
            this.getFinishedOrganizedTournaments().add(tournament.getName());
    }
}
