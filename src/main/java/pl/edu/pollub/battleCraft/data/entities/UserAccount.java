package pl.edu.pollub.battleCraft.data.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserAccount extends AddressOwner implements Serializable {

    public UserAccount(String name){
        this.name = name;
    }

    public UserAccount(String name, List<Participation> participatedTournaments){
        this.name = name;
        this.participatedTournaments = participatedTournaments;
    }

    @Column(length = 30, unique = true)
    private String name;

    @OneToMany(cascade={CascadeType.ALL}, mappedBy="user")
    private List<Participation> participatedTournaments;
}
