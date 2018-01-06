package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponseDTO {
    private String name;
    private boolean accepted;
}
