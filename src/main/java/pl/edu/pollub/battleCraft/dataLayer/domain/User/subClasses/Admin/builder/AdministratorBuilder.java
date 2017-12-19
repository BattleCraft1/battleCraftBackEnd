package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.builder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Admin.Administrator;

@Component
public class AdministratorBuilder {
    private Administrator instance;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public Administrator build() {
        return instance;
    }

    public AdministratorBuilder create(String firstname, String lastname, String name, String email){
        instance = new Administrator();
        this.instance.setFirstname(firstname);
        this.instance.setLastname(lastname);
        this.instance.setName(name);
        this.instance.setEmail(email);
        return this;
    }

    public AdministratorBuilder setPassword(String password){
        this.instance.setPassword(bCryptPasswordEncoder.encode(password));
        return this;
    }

    public AdministratorBuilder from(Address address) {
        this.instance.getAddressOwnership().insertAddress(address);
        return this;
    }

    public AdministratorBuilder withPhoneNumber(String phoneNumber) {
        this.instance.setPhoneNumber(phoneNumber);
        return this;
    }
}
