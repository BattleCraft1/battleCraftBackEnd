package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountRequestDTO extends AddressOwnerRequestDTO {
    public String name;
    public String nameChange;
    public String email;
    public String firstname;
    public String lastname;
    public String phoneNumber;
    public List<InvitationDTO> participatedTournaments = new ArrayList<>();
    public List<InvitationDTO> organizedTournaments = new ArrayList<>();
}
