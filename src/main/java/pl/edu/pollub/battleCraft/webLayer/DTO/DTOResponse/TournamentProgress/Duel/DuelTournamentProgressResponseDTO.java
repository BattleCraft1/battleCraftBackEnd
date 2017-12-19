package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.Battle.DuelBattleResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.TournamentProgressResponseDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DuelTournamentProgressResponseDTO implements TournamentProgressResponseDTO{
    private List<List<DuelBattleResponseDTO>> turns = new ArrayList<>();
    private Map<String,Integer> playersNamesWithPoints = new HashMap<>();
    private Map<Integer,List<String>> playersWithoutBattles = new HashMap<>();
    private int currentTurnNumber;
    private TournamentStatus tournamentStatus;
    private int playersOnTableCount = 2;
    private int playersCount;
    private boolean canCurrentUserMenageTournament;
}
