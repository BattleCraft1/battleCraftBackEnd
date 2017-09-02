package pl.edu.pollub.battleCraft.data.entities.User.subClasses.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.DiscriminatorFormula;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorFormula("CASE WHEN user_type IS NOT NULL THEN 'ORGANIZER' ELSE 'PLAYER' end")
@SecondaryTable(name = "player", pkJoinColumns = {@PrimaryKeyJoinColumn(name="id", referencedColumnName = "id")})
@DiscriminatorColumn(name="user_type", discriminatorType=DiscriminatorType.STRING)

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Player extends UserAccount{

    public Player(){
        super();
        this.banned=false;
    }

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL , mappedBy="player")
    private List<Participation> participatedTournaments;

    private boolean banned;

    public void addParticipatedTournaments(Tournament... tournaments){
        this.participatedTournaments.addAll(Arrays.stream(tournaments)
                .map(tournament -> new Participation(this,tournament))
                .collect(Collectors.toList()));
    }
}
