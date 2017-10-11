package pl.edu.pollub.battleCraft.dataLayer.entities.Address;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Province{
    Lubelskie("lubelskie"),
    Dolnoslaskie("dolnośląskie"),
    Malopolskie("małopolskie"),
    Slaskie("śląskie"),
    Zachodniopomorskie("zachodiopomorskie"),
    Wielkopolskie("wielkopolskie"),
    Opolskie("opolskie"),
    Lodzkie("łódzkie"),
    Podlaskie("podlaskie");

    String location;

    Province(String location) {
        this.location = location;
    }

    public static List<String> getNames() {
        return Arrays.stream(Province.values()).map(Enum::name).collect(Collectors.toList());
    }

    public boolean equalsName(String otherName) {
        return location.equals(otherName);
    }
}
