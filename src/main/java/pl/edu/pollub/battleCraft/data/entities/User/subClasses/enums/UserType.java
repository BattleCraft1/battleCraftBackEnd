package pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum UserType {
    NEW(Values.NEW),PLAYER(Values.PLAYER),ORGANIZER(Values.ORGANIZER),ADMIN(Values.ADMIN);

    String name;

    UserType(String name) {
        this.name = name;
    }

    public static List<String> getNames() {
        return Arrays.stream(UserType.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public static class Values {
        public static final String NEW = "NEW";
        public static final String PLAYER = "PLAYER";
        public static final String ORGANIZER = "ORGANIZER";
        public static final String ADMIN = "ADMIN";
    }
}
