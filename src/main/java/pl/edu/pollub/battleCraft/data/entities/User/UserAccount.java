package pl.edu.pollub.battleCraft.data.entities.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Address.AddressOwner;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;

import javax.persistence.*;
import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserAccount extends AddressOwner{

    public UserAccount(){
        super();
        this.userType = UserType.NEW;
    }

    protected UserAccount(UserType userType){
        super();
        this.userType = userType;
    }

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column(length = 20)
    private String name;

    @Column(length = 20)
    private String surname;

    @Column(length = 30, unique = true)
    private String username;

    @Column(length = 50, unique = true)
    private String email;

    @JsonIgnore
    @Column(length = 100)
    private String password;

    @Column(length = 11)
    private String phoneNumber;
}
