package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.enums;

import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ToString
public enum UserType {
    NEW(Values.NEW), ACCEPTED(Values.ACCEPTED), ORGANIZER(Values.ORGANIZER), ADMIN(Values.ADMIN);

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
        public static final String ACCEPTED = "ACCEPTED";
        public static final String ORGANIZER = "ORGANIZER";
        public static final String ADMIN = "ADMIN";
    }
}
