package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestPlayerDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRequestDTO extends AddressOwnerRequestDTO {
    private String name;
    private String nameChange;
    private String email;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private List<InvitationRequestPlayerDTO> participatedTournaments = new ArrayList<>();
    private List<InvitationRequestDTO> organizedTournaments = new ArrayList<>();
}
