package pl.edu.pollub.battleCraft.dataLayer.entities.User;

import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.Province;

public class UserBuilder {
    private UserAccount instance;

    public UserBuilder() {
        this.instance = new UserAccount();
    }

    public UserBuilder create(String firstname, String lastname, String name, String email) {
        this.instance = new UserAccount();
        this.setBasicData(firstname,lastname,name,email);
        return this;
    }

    public UserBuilder edit(UserAccount userAccount, String firstname, String lastname, String name, String email){
        this.instance = userAccount;
        this.setBasicData(firstname,lastname,name,email);
        return this;
    }

    private void setBasicData(String firstname, String lastname, String name, String email){
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
    }

    public UserBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public UserBuilder from(Address address) {
        this.instance.changeAddress(address);
        return this;
    }

    public UserBuilder changeAddress(String province, String city, String street, String zipCode, String description) {
        instance.changeAddress(province, city, street, zipCode, description);
        return this;
    }

    public UserAccount build() {
        return instance;
    }
}
