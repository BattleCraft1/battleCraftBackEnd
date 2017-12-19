package pl.edu.pollub.battleCraft.dataLayer.domain.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwnership.AddressOwnership;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken.VerificationToken;

import javax.persistence.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@DiscriminatorValue("User")
@DiscriminatorColumn(name= "role", discriminatorType=DiscriminatorType.STRING)

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class UserAccount{

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    public UserAccount(){
        super();
        this.status = UserType.NEW;
        this.addressOwnership = new AddressOwnership();
    }

    protected UserAccount(UserType userType){
        super();
        this.status = userType;
        this.addressOwnership = new AddressOwnership();
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

    @Embedded
    private AddressOwnership addressOwnership;
}
