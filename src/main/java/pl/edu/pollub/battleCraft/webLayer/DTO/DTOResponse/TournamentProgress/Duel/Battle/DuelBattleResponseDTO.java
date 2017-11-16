package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.Battle;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayerDTO;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DuelBattleResponseDTO {
    private int tableNumber;
    private PlayerDTO firstPlayer;
    private PlayerDTO secondPlayer;
}
