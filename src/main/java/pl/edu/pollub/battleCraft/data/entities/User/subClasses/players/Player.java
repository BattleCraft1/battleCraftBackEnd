package pl.edu.pollub.battleCraft.data.entities.User.subClasses.players;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Player extends UserAccount {

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "player")
    private List<Participation> participatedTournaments = new ArrayList<>();

    private boolean banned;

    public Player() {
        super(UserType.PLAYER);
        this.banned = false;
    }

    protected Player(UserType userType) {
        super(userType);
        this.banned = false;
    }

    public void addParticipation(Participation participation) {
        this.participatedTournaments.add(participation);
    }

    private void setParticipatedTournaments(List<Participation> participatedTournaments){
        this.participatedTournaments = participatedTournaments;
    }
}
