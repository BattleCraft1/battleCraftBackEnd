package pl.edu.pollub.battleCraft.data.entities.User;

import pl.edu.pollub.battleCraft.data.entities.Address.Address;

public class UserBuilder {
    private UserAccount instance;

    public UserBuilder() {
        this.instance = new UserAccount();
    }

    public UserBuilder create(String name, String surname, String username, String email, String password) {
        this.instance = new UserAccount();
        this.instance.setName(name);
        this.instance.setSurname(surname);
        this.instance.setUsername(username);
        this.instance.setEmail(email);
        this.instance.setPassword(password);
        return this;
    }

    public UserBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }

    public UserBuilder from(Address address) {
        this.instance.changeAddress(address);
        return this;
    }

    public UserAccount build() {
        return instance;
    }
}
