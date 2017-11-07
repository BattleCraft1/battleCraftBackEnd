package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<InvitationDTO> participatedTournaments = new ArrayList<>();
    private List<String> finishedParticipatedTournaments = new ArrayList<>();
    private List<InvitationDTO> organizedTournaments = new ArrayList<>();
    private List<String> finishedOrganizedTournaments = new ArrayList<>();
    private List<String> createdGames = new ArrayList<>();
    private boolean banned;
}
