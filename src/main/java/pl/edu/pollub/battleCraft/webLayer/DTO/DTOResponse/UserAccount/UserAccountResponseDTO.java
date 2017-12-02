package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.PlayerFinishedInvitationResponse;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.PlayerGroupFinishedInvitationResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.InvitationResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation.PlayerInvitationResponseDTO;

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
    private List<PlayerFinishedInvitationResponse> finishedParticipatedTournaments = new ArrayList<>();
    private List<InvitationResponseDTO> organizedTournaments = new ArrayList<>();
    private List<String> finishedOrganizedTournaments = new ArrayList<>();
    private List<String> createdGames = new ArrayList<>();
    private boolean banned;
    private boolean canCurrentUserEdit;
    private String newToken;
}
