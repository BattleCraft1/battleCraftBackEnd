package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.builder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;

@Component
public class OrganizerBuilder {
    private Organizer instance;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public OrganizerBuilder create(String firstname, String lastname, String name, String email) {
        this.instance = new Organizer();
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
        return this;
    }

    public OrganizerBuilder setPassword(String password){
        this.instance.setPassword(bCryptPasswordEncoder.encode(password));
        return this;
    }

    public OrganizerBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public OrganizerBuilder from(Address address) {
        this.instance.getAddressOwnership().insertAddress(address);
        return this;
    }

    public Organizer build() {
        return instance;
    }

}
