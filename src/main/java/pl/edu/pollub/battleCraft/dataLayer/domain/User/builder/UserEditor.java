package pl.edu.pollub.battleCraft.dataLayer.domain.User.builder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;

@Component
public class UserEditor{

    private final UserBuilder userBuilder;

    @Autowired
    public UserEditor(UserBuilder userBuilder) {
        this.userBuilder = userBuilder;
    }

    public UserEditor edit(UserAccount userAccount, String firstname, String lastname, String name, String email){
        this.userBuilder.setInstance(userAccount);
        this.userBuilder.setBasicData(firstname,lastname,name,email);
        return this;
    }

    public UserEditor withPhoneNumber(String phoneNumber) {
        this.userBuilder.withPoneNumber(phoneNumber);
        return this;
    }

    public UserEditor changeAddress(String province, String city, String street, String zipCode, String description) {
        this.userBuilder.build().getAddressOwnership().changeAddress(province, city, street, zipCode, description);
        return this;
    }

    public UserAccount build() {
        return userBuilder.build();
    }
}
