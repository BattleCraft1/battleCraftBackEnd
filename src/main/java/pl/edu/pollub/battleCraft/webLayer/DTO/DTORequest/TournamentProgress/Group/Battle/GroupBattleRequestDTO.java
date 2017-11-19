package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress.Group.Battle;

import lombok.*;
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
    private PlayersGroupDTO secondPlayersGroup;
    private int tourNumber;

    public boolean containsDuplicatedNames(){
        List<String> names = new ArrayList<>();
        names.addAll(firstPlayersGroup.getPlayersNames());
        names.addAll(secondPlayersGroup.getPlayersNames());
        Set<String> setOfNamesWithoutDuplicates = new HashSet<>(names);
        return setOfNamesWithoutDuplicates.size() > names.size() && !setOfNamesWithoutDuplicates.contains("");
    }

    public boolean containsEmptyName(){
        return firstPlayersGroup.getPlayersNames().get(0).equals("") ||
                firstPlayersGroup.getPlayersNames().get(1).equals("") ||
                secondPlayersGroup.getPlayersNames().get(0).equals("") ||
                secondPlayersGroup.getPlayersNames().get(1).equals("");
    }
}
