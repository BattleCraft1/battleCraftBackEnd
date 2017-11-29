package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Battle.GroupBattleResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Points.PlayersGroupWithPointsDTO;
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
public class GroupTournamentProgressResponseDTO implements TournamentProgressResponseDTO {
    private List<List<GroupBattleResponseDTO>> tours = new ArrayList<>();
    private List<PlayersGroupWithPointsDTO> playersNamesWithPoints = new ArrayList<>();
    private Map<Integer,List<List<String>>> playersWithoutBattles = new HashMap<>();
    private int currentTourNumber;
    private TournamentStatus tournamentStatus;
    private int playersOnTableCount = 4;
    private int playersCount;
    private boolean canCurrentUserMenageTournament;
}
