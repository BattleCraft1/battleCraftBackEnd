package pl.edu.pollub.battleCraft.data.entities.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Address.AddressOwner;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="user_type", discriminatorType=DiscriminatorType.STRING)

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserAccount extends AddressOwner implements Serializable {

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
