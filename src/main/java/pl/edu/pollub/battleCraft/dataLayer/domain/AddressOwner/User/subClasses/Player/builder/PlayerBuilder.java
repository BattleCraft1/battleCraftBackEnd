package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.builder;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;

@Component
public class PlayerBuilder {
    private Player instance;

    public PlayerBuilder create(String firstname, String lastname, String name, String email) {
        this.instance = new Player();
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
        return this;
    }

    public PlayerBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public PlayerBuilder from(Address address) {
        this.instance.initAddress(address);
        return this;
    }

    public Player build() {
        return instance;
    }

}
