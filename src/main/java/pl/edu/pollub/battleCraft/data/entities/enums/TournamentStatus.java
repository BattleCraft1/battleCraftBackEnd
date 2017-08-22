package pl.edu.pollub.battleCraft.data.entities.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TournamentStatus {
    NEW, ACCEPTED, IN_PROGRESS, FINISHED;

    public static List<String> getNames() {
        return Arrays.stream(TournamentStatus.values()).map(Enum::name).collect(Collectors.toList());
    }
}
