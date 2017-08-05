package pl.edu.pollub.battleCraft.data.entities.enums;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TournamentClass  implements Serializable{
    LOCAL, CHALLENGER, MASTER;

    public static List<String> getNames() {
        return Arrays.stream(TournamentClass.values()).map(Enum::name).collect(Collectors.toList());
    }
}
