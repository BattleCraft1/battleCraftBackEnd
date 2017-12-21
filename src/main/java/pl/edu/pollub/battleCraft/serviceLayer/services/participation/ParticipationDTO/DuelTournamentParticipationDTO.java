package pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class DuelTournamentParticipationDTO extends ParticipationDTO {
    public DuelTournamentParticipationDTO(Tournament tournament, boolean accepted) {
        super(tournament, accepted);
    }
}
