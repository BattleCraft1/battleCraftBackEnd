package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequestPlayerDTO {
    private InvitationRequestDTO firstPlayerInvitation;
    private String secondPlayerName;
}
