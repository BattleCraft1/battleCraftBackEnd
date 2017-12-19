package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.emuns.ColorOfSideInBattle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Play {

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int points;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "battle_id")
    private Battle battle;
    
    public Play(Player player,Battle battle, int points, ColorOfSideInBattle colorOfSideInBattle){
        this.player = player;
        this.battle = battle;
        this.points = points;
        this.colorOfSideInBattle = colorOfSideInBattle;
    }

    public Play(Player player,Battle battle, ColorOfSideInBattle colorOfSideInBattle){
        this.player = player;
        this.battle = battle;
        this.points = 0;
        this.colorOfSideInBattle = colorOfSideInBattle;
    }

    @Enumerated(EnumType.STRING)
    private ColorOfSideInBattle colorOfSideInBattle;
}
