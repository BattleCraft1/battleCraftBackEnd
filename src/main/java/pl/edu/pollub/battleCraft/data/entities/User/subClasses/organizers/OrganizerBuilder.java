package pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers;

import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;

public class OrganizerBuilder {
    private Organizer instance;

    public OrganizerBuilder() {

    }

    public OrganizerBuilder create(String name, String surname, String username, String email, String password) {
        this.instance = new Organizer();
        this.instance.setName(name);
        this.instance.setSurname(surname);
        this.instance.setUsername(username);
        this.instance.setEmail(email);
        this.instance.setPassword(password);
        return this;
    }


    public OrganizerBuilder organize(Tournament... tournaments) {
        this.instance.setOrganizedTournaments(tournaments);
        return this;
    }

    public OrganizerBuilder participateTo(Tournament... tournaments) {
        this.instance.setParticipatedTournaments(tournaments);
        return this;
    }

    public OrganizerBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public OrganizerBuilder from(Address address) {
        this.instance.setAddress(address);
        return this;
    }

    public Organizer build() {
        return instance;
    }
}
