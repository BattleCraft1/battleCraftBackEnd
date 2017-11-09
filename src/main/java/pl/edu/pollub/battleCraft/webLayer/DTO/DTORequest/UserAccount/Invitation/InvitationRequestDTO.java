package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequestDTO {
    private String tournamentName;
    private boolean accepted;
}

