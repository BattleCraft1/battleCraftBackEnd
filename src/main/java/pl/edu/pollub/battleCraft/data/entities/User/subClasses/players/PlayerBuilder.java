package pl.edu.pollub.battleCraft.data.entities.User.subClasses.players;

import pl.edu.pollub.battleCraft.data.entities.Address.Address;

public class PlayerBuilder {
    private Player instance;

    public PlayerBuilder() {

    }

    public PlayerBuilder create(String firstname, String lastname, String name, String email, String password) {
        this.instance = new Player();
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
        this.instance.setPassword(password);
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
