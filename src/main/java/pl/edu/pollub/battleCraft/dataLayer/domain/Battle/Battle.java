package pl.edu.pollub.battleCraft.dataLayer.domain.Battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.emuns.ColorOfSideInBattle;
import pl.edu.pollub.battleCraft.dataLayer.domain.Turn.Turn;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Play;

import javax.persistence.*;
import java.util.ArrayList;
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

    public Battle(Turn tour, int tableNumber){
        this.tableNumber = tableNumber;
        this.setTurn(tour);
        this.finished = false;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int tableNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "turn_id")
    private Turn turn;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "battle")
    private List<Play> players = new ArrayList<>();

    boolean finished;

    public void setPoints( Player firstPlayer, int firstPlayerPoints, Player secondPlayer, int secondPlayerPoints){
        Play firstPlayerPlay = new Play(firstPlayer,this, firstPlayerPoints, ColorOfSideInBattle.BLUE);
        Play secondPlayerPlay = new Play(secondPlayer, this,secondPlayerPoints, ColorOfSideInBattle.RED);

        this.clearPlayers();
        this.players.add(firstPlayerPlay);
        this.players.add(secondPlayerPlay);

        firstPlayer.addBattleByOneSide(firstPlayerPlay);
        secondPlayer.addBattleByOneSide(secondPlayerPlay);

        this.finished = true;
    }

    public void setPoints(List<Player> firstPlayersGroup, int firstGroupPoints, List<Player> secondPlayersGroup, int secondGroupPoints){
        Play firstPlayerInFirstGroupPlay = new Play(firstPlayersGroup.get(0),this, firstGroupPoints, ColorOfSideInBattle.BLUE);
        Play secondPlayerInFirstGroupPlay = new Play(firstPlayersGroup.get(1),this, firstGroupPoints, ColorOfSideInBattle.BLUE);
        Play firstPlayerInSecondGroupPlay = new Play(secondPlayersGroup.get(0), this, secondGroupPoints, ColorOfSideInBattle.RED);
        Play secondPlayerInSecondGroupPlay = new Play(secondPlayersGroup.get(1), this,secondGroupPoints, ColorOfSideInBattle.RED);

        this.clearPlayers();
        this.players.add(firstPlayerInFirstGroupPlay);
        this.players.add(secondPlayerInFirstGroupPlay);
        this.players.add(firstPlayerInSecondGroupPlay);
        this.players.add(secondPlayerInSecondGroupPlay);

        firstPlayersGroup.get(0).addBattleByOneSide(firstPlayerInFirstGroupPlay);
        firstPlayersGroup.get(1).addBattleByOneSide(secondPlayerInFirstGroupPlay);
        secondPlayersGroup.get(0).addBattleByOneSide(firstPlayerInSecondGroupPlay);
        secondPlayersGroup.get(1).addBattleByOneSide(secondPlayerInSecondGroupPlay);


        this.finished = true;
    }

    public void addPlayers(Player firstPlayer, Player secondPlayer) {
        Play firstPlayerPlay = new Play(firstPlayer,this, ColorOfSideInBattle.BLUE);
        Play secondPlayerPlay = new Play(secondPlayer, this, ColorOfSideInBattle.RED);

        this.clearPlayers();
        this.players.add(firstPlayerPlay);
        this.players.add(secondPlayerPlay);

        firstPlayer.addBattleByOneSide(firstPlayerPlay);
        secondPlayer.addBattleByOneSide(secondPlayerPlay);
    }

    public void clearPlayers(){
        this.players.forEach(play -> play.getPlayer().removeBattleByOneSide(play));
        this.players.clear();

        this.finished = false;
    }

    public void addAlonePlayer(Player player){
        Play play = new Play(player,this, NUMBER_OF_POINTS_FOR_ALONE_PLAYER, ColorOfSideInBattle.BLUE);

        this.clearPlayers();
        this.players.add(play);

        player.addBattleByOneSide(play);

        this.finished = true;
    }

    public void addAlonePlayer(List<Player> player){
        Play playOfFirstPlayer = new Play(player.get(0),this, NUMBER_OF_POINTS_FOR_ALONE_PLAYER, ColorOfSideInBattle.BLUE);
        Play playOfSecondPlayer = new Play(player.get(1),this, NUMBER_OF_POINTS_FOR_ALONE_PLAYER, ColorOfSideInBattle.BLUE);

        this.clearPlayers();
        this.players.add(playOfFirstPlayer);
        this.players.add(playOfSecondPlayer);

        player.get(0).addBattleByOneSide(playOfFirstPlayer);
        player.get(1).addBattleByOneSide(playOfSecondPlayer);

        this.finished = true;
    }

    public void addPlayers(List<Player> firstPlayersGroup, List<Player> secondPlayersGroup) {
        Play firstPlayerInFirstGroupPlay = new Play(firstPlayersGroup.get(0),this, ColorOfSideInBattle.BLUE);
        Play secondPlayerInFirstGroupPlay = new Play(firstPlayersGroup.get(1),this, ColorOfSideInBattle.BLUE);
        Play firstPlayerInSecondGroupPlay = new Play(secondPlayersGroup.get(0), this, ColorOfSideInBattle.RED);
        Play secondPlayerInSecondGroupPlay = new Play(secondPlayersGroup.get(1), this, ColorOfSideInBattle.RED);

        this.clearPlayers();
        this.players.add(firstPlayerInFirstGroupPlay);
        this.players.add(secondPlayerInFirstGroupPlay);
        this.players.add(firstPlayerInSecondGroupPlay);
        this.players.add(secondPlayerInSecondGroupPlay);

        firstPlayersGroup.get(0).addBattleByOneSide(firstPlayerInFirstGroupPlay);
        firstPlayersGroup.get(1).addBattleByOneSide(secondPlayerInFirstGroupPlay);
        secondPlayersGroup.get(0).addBattleByOneSide(firstPlayerInSecondGroupPlay);
        secondPlayersGroup.get(1).addBattleByOneSide(secondPlayerInSecondGroupPlay);
    }

}
