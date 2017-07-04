package pl.edu.pollub.battleCraft.entities.enums;

/**
 * Created by Jarek on 2017-07-04.
 */
public enum Provinces {
    MAZOWIECKIE("Mazowieckie"),
    SLASKIE("Śląskie"),
    WIELKOPOLSKIE("Wielkopolskie"),
    MALOPOLSKIE("małopolskie"),
    DOLNOSLASKIE("dolnośląskie"),
    LODZKIE("łódzkie"),
    POMORSKIE("pomorskie"),
    LUBELSKIE("lubelskie"),
    PODKARPACKIE("podkarpackie"),
    KUJAWSKO_POMORSKIE("kujawsko-pomorskie"),
    ZACHODNIO_POMORSKIE("zachodniopomorskie"),
    WARMINSKO_MAZURSKIE("warmińsko-mazurskie"),
    SWIETOKRZYSKIE("świętokrzyskie"),
    PODLASKIE("podlaskie"),
    LUBUSKIE("lubuskie"),
    OPOLSKIE("opolskie");

    String location;

    private Provinces(final String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return location;
    }
}
