package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TournamentType {
    DUEL(2), GROUP(4);

    int playersOnTableCount;

    TournamentType(int playersOnTableCount) {
        this.playersOnTableCount = playersOnTableCount;
    }

    public static List<String> getNames() {
        return Arrays.stream(TournamentStatus.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(int playersOnTableCount) {
        return this.playersOnTableCount == playersOnTableCount;
    }

    public int value(){
        return this.playersOnTableCount;
    }
}
