package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.builder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

@Component
public class PlayerBuilder {
    private Player instance;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public PlayerBuilder create(String firstname, String lastname, String name, String email) {
        this.instance = new Player();
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
        return this;
    }

    public PlayerBuilder setPassword(String password){
        this.instance.setPassword(bCryptPasswordEncoder.encode(password));
        return this;
    }

    public PlayerBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public PlayerBuilder from(Address address) {
        this.instance.getAddressOwnership().insertAddress(address);
        return this;
    }

    public Player build() {
        return instance;
    }

}
