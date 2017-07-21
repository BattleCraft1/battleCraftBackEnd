package pl.edu.pollub.battleCraft.entities.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TournamentClass {
    LOCAL, CHALLENGER, MASTER;

    public static List<String> getNames() {
        return Arrays.stream(TournamentClass.values()).map(Enum::name).collect(Collectors.toList());
    }
}
