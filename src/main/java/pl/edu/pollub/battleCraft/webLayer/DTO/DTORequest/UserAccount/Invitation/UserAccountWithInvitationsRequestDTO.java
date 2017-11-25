package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Address.AddressOwnerRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestPlayerDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.UserAccountRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountWithInvitationsRequestDTO extends UserAccountRequestDTO {

    private List<InvitationRequestPlayerDTO> participatedTournaments = new ArrayList<>();
    private List<InvitationRequestDTO> organizedTournaments = new ArrayList<>();
}
