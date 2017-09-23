package pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships;

import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Participation implements Cloneable{
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public Participation(Player player, Tournament tournament) {
        this.player = player;
        this.tournament = tournament;
    }

    public Participation clone(){
        return new Participation(this.player,this.tournament);
    }
}
