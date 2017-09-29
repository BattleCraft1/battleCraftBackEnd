package pl.edu.pollub.battleCraft.data.entities.Battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Tour.Tour;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Battle {

    public Battle(int tableNumber){
        this.tableNumber = tableNumber;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int tableNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "battle")
    private List<Play> players = new ArrayList<>();

    @JsonIgnore
    @Transient
    public void setPoints(int points){
        players.get(0).setPoints(points);
        players.get(1).setPoints(20 - points);
    }

    public void setTour(Tour tour){
        this.tour = tour;
        this.tour.addBattleByOneSide(this);
    }

    public void addPlayers(Player firstPlayer, Player secondPlayer){
        Play firstPlayerPlay = new Play(secondPlayer,this);
        Play secondPlayerPlay = new Play(firstPlayer, this);
        this.players.addAll(
                Arrays.asList(
                        firstPlayerPlay,
                        secondPlayerPlay
                )
        );
        firstPlayer.addBattlesByOneSide(firstPlayerPlay);
        secondPlayer.addBattlesByOneSide(secondPlayerPlay);
    }

    public void addPlayersByOneSide(Play firstPlayer, Play secondPlayer){
        this.players.addAll(Arrays.asList(firstPlayer,secondPlayer));
    }

    public void setTourByOneSide(Tour tour){
        this.tour = tour;
    }
}
