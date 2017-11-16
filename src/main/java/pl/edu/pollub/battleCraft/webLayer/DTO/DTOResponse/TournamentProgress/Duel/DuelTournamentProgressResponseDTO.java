package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Duel.Battle.DuelBattleResponseDTO;

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
public class DuelTournamentProgressResponseDTO {
    private List<List<DuelBattleResponseDTO>> tours = new ArrayList<>();
    private Map<String,Integer> playersNamesWithPoints = new HashMap<>();
    private Map<Integer,List<String>> playersWithoutBattles = new HashMap<>();
    private int currentTourNumber;
    private TournamentStatus tournamentStatus;
}