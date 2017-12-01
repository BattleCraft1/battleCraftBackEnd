package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.AddressOwner;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken.VerificationToken;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

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

    @Column(length = 20,nullable = false)
    protected String firstname;

    @Column(length = 20,nullable = false)
    protected String lastname;

    @Column(length = 30, unique = true, nullable = false)
    protected String name;

    @Column(length = 50, unique = true, nullable = false)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(length = 11)
    protected String phoneNumber;

    @OneToOne(mappedBy = "user")
    private VerificationToken token;

    protected boolean banned = false;

    protected void setBanned(boolean banned) {
        this.banned = banned;
    }
}
