package pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {
    protected Tournament tournament;
    protected boolean accepted;
}
