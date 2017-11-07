package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.builder;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;

@Component
public class UserBuilder {
    private UserAccount instance;

    public UserAccount getInstance() {
        return instance;
    }

    public void setInstance(UserAccount instance) {
        this.instance = instance;
    }

    void setBasicData(String firstname, String lastname, String name, String email){
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
    }

    public UserBuilder withPoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

}
