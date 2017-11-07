package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {
    private String name;
    private boolean accepted;
}
