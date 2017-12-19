package pl.edu.pollub.battleCraft.dataLayer.domain.User.builder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;

@Component
class UserBuilder {
    private UserAccount instance;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    UserAccount build() {
        return instance;
    }

    void setInstance(UserAccount instance) {
        this.instance = instance;
    }

    void setBasicData(String firstname, String lastname, String name, String email){
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
    }

    UserBuilder setPassword(String password){
        this.instance.setPassword(bCryptPasswordEncoder.encode(password));
        return this;
    }

    UserBuilder withPoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }
}
