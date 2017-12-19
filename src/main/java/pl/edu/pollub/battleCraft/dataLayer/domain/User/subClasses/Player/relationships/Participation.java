package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
public class Participation{

    public Participation(Player player, Tournament participatedTournament, Long groupNumber) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = true;//true only for test
        this.groupNumber = groupNumber;
    }

    private Participation(Player player, Tournament participatedTournament, boolean accepted, Long groupNumber) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = accepted;
        this.groupNumber = groupNumber;
    }
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "player_id")
    protected Player player;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tournament_id")
    protected Tournament participatedTournament;

    protected boolean accepted;

    protected Long groupNumber;

    @JsonIgnore
    public Participation copy(){
        return new Participation(this.player,this.participatedTournament,this.accepted,this.groupNumber);
    }
}
