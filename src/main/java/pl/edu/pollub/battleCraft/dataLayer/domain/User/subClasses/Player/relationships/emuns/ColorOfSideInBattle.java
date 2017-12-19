package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.emuns;

import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
public enum ColorOfSideInBattle {
    NONE("NONE"), RED("RED"), BLUE("BLUE");

    String name;

    ColorOfSideInBattle(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(TournamentStatus.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }
}
