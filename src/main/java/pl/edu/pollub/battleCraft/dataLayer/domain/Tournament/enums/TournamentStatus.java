package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums;

import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
public enum TournamentStatus {
    NEW("NEW"), ACCEPTED("ACCEPTED"), IN_PROGRESS("IN_PROGRESS"), FINISHED("FINISHED");

    String name;

    TournamentStatus(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(TournamentStatus.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }
}
