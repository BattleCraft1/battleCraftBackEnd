package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.AddressOwner;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;

import javax.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public class UserAccount extends AddressOwner{

    public UserAccount(){
        super();
        this.status = UserType.NEW;
    }

    protected UserAccount(UserType userType){
        super();
        this.status = userType;
    }

    @Enumerated(EnumType.STRING)
    protected UserType status;

    @Column(length = 20)
    protected String firstname;

    @Column(length = 20)
    protected String lastname;

    @Column(length = 30, unique = true)
    protected String name;

    @Column(length = 50, unique = true)
    protected String email;

    @JsonIgnore
    @Column(length = 100)
    protected String password;

    @Column(length = 11)
    protected String phoneNumber;

}
