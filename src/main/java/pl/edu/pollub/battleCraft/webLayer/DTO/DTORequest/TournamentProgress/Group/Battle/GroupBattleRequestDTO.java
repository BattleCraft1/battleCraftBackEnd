package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle;

import lombok.*;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayerDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.PlayersGroupDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupBattleRequestDTO {
    private int tableNumber;
    private PlayersGroupDTO firstPlayersGroup;
    private PlayersGroupDTO secondPlayerGroup;
    private int tourNumber;

    public boolean containsDuplicatedNames(){
        List<String> names = new ArrayList<>();
        names.addAll(firstPlayersGroup.getPlayersNames());
        names.addAll(secondPlayerGroup.getPlayersNames());
        Set<String> setOfNamesWithoutDuplicates = new HashSet<>(names);
        return setOfNamesWithoutDuplicates.size() > names.size();
    }

    public boolean containsEmptyName(){
        return firstPlayersGroup.getPlayersNames().get(0).equals("") ||
                firstPlayersGroup.getPlayersNames().get(1).equals("") ||
                secondPlayerGroup.getPlayersNames().get(0).equals("") ||
                secondPlayerGroup.getPlayersNames().get(1).equals("");
    }
}
