package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
public class Participation implements Cloneable{

    public Participation(Player player, Tournament participatedTournament) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = false;
    }

    private Participation(Player player, Tournament participatedTournament, boolean accepted) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = accepted;
    }
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tournament_id")
    private Tournament participatedTournament;

    private boolean accepted;

    public Participation clone(){
        return new Participation(this.player,this.participatedTournament,this.accepted);
    }
}
