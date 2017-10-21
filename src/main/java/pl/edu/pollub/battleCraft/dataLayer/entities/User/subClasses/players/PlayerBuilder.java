package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players;

import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerBuilder {
    private Player instance;

    public PlayerBuilder() {

    }

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
        this.instance.changeAddress(address);
        return this;
    }

    public Player build() {
        return instance;
    }

}
