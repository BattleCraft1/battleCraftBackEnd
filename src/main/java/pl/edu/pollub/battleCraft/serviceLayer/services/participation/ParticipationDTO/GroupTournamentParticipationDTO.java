package pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupTournamentParticipationDTO extends ParticipationDTO {
    public GroupTournamentParticipationDTO(Tournament tournament, boolean accepted, Player player) {
        super(tournament, accepted);
        this.secondPlayer = player;

    }
    private Player secondPlayer;
}
