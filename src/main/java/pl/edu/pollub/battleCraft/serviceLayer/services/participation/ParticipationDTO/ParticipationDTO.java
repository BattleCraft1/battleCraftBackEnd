package pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationDTO {
    protected Tournament tournament;
    protected boolean accepted;
}
