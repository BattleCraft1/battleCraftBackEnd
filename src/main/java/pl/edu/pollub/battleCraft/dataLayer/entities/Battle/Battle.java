package pl.edu.pollub.battleCraft.dataLayer.entities.Battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tour.Tour;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Play;

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

    @Transient
    private final int NUMBER_OF_POINTS_FOR_ALONE_PLAYER = 17;

    public Battle(Tour tour, int tableNumber){
        this.tableNumber = tableNumber;
        this.setTour(tour);
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

    public void setPoints(int points){
        this.getFirstPlay().setPoints(points);
        this.getSecondPlay().setPoints(20 - points);
    }

    @JsonIgnore
    public Play getFirstPlay(){
        return players.get(0);
    }

    @JsonIgnore
    public Play getSecondPlay(){
        return players.get(1);
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

    public void addAlonePlayer(Player player){
        Play play = new Play(player,this);
        play.setPoints(NUMBER_OF_POINTS_FOR_ALONE_PLAYER);
        this.players.add(play);
        player.addBattlesByOneSide(play);
    }

    public void setTourByOneSide(Tour tour){
        this.tour = tour;
    }
}
