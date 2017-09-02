package pl.edu.pollub.battleCraft.data.entities.Tournament.enums;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TournamentClass  implements Serializable{
    LOCAL("LOCAL"), CHALLENGER("CHALLENGER"), MASTER("MASTER");

    String name;

    TournamentClass(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(TournamentClass.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }
}
