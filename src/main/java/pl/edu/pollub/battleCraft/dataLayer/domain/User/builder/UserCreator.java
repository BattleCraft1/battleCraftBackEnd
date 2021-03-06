package pl.edu.pollub.battleCraft.dataLayer.domain.User.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.Administrator;

@Component
public class UserCreator{

    private final UserBuilder userBuilder;

    @Autowired
    public UserCreator(UserBuilder userBuilder) {
        this.userBuilder = userBuilder;
    }

    public UserCreator create(String firstname, String lastname, String name, String email) {
        userBuilder.setInstance(new UserAccount());
        userBuilder.setBasicData(firstname,lastname,name,email);
        return this;
    }

    public UserCreator withPhoneNumber(String phoneNumber) {
        this.userBuilder.withPoneNumber(phoneNumber);
        return this;
    }

    public UserCreator setPassword(String password){
        this.userBuilder.setPassword(password);
        return this;
    }

    public UserCreator from(Address address) {
        this.userBuilder.build().getAddressOwnership().insertAddress(address);
        return this;
    }

    public UserAccount build() {
        return userBuilder.build();
    }
}
