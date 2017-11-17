package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Battle;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayerDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayersGroupDTO;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupBattleResponseDTO {
    private int tableNumber;
    private PlayersGroupDTO firstPlayersGroup;
    private PlayersGroupDTO secondPlayersGroup;
}
