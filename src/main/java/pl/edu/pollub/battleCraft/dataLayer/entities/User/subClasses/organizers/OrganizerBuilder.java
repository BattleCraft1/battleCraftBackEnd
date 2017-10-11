package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers;

import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;

public class OrganizerBuilder {
    private Organizer instance;

    public OrganizerBuilder() {

    }

    public OrganizerBuilder create(String firstname, String lastname, String name, String email, String password) {
        this.instance = new Organizer();
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
        this.instance.setPassword(password);
        return this;
    }

    public OrganizerBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public OrganizerBuilder from(Address address) {
        this.instance.changeAddress(address);
        return this;
    }

    public Organizer build() {
        return instance;
    }
}
