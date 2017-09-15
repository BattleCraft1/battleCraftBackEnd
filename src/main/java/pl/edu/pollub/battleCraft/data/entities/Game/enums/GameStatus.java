package pl.edu.pollub.battleCraft.data.entities.Game.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum GameStatus {
    NEW("NEW"), ACCEPTED("ACCEPTED");

    String name;

    GameStatus(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(GameStatus.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }
}
