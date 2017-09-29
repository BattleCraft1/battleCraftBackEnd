package pl.edu.pollub.battleCraft.data.entities.Tour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.data.entities.Tournament.subClasses.tournamentWithProgression.TournamentWithProgression;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.start.BattleWithTableNumberNotFound;

import javax.persistence.*;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Tour {

    public Tour(int number,TournamentWithProgression tournamentWithProgression){
        this.number = number;
        this.setTournament(tournamentWithProgression);
        int battlesNumber;
        if(tournamentWithProgression.getMaxPlayers()%2==0){
            battlesNumber = tournamentWithProgression.getMaxPlayers()/2;
        }
        else{
            battlesNumber = tournamentWithProgression.getMaxPlayers()/2+1;
        }

        for(int i=0;i<battlesNumber;i++){
            battles.add(new Battle(i));
        }
    }

    @JsonIgnore
    @Transient
    public void randomizePlayersOnTableInFirstTour(int tableNumber, List<Player> playersWithoutBattle) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        int firstPlayerRandomIndex = randomGenerator.nextInt(playersWithoutBattle.size());
        Player firstPlayer = playersWithoutBattle.get(firstPlayerRandomIndex);
        playersWithoutBattle.remove(firstPlayerRandomIndex);
        int secondPlayerRandomIndex = randomGenerator.nextInt(playersWithoutBattle.size());
        Player secondPlayer = playersWithoutBattle.get(secondPlayerRandomIndex);
        battleWithTableNumber.addPlayers(firstPlayer,secondPlayer);
    }

    @JsonIgnore
    @Transient
    public void setPlayersOnTableInFirstTour(int tableNumber, Player firstPlayer, Player secondPlayer) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.addPlayers(firstPlayer,secondPlayer);
    }

    @JsonIgnore
    @Transient
    public void setPointsOnTable(int tableNumber, int pointsForFirstPlayer){
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.setPoints(pointsForFirstPlayer);
    }

    private Battle findBattleByTableNumber(int tableNumber){
        return  this.battles.stream()
                .filter(battle -> battle.getTableNumber()==tableNumber)
                .findFirst().orElseThrow(() -> new BattleWithTableNumberNotFound(tableNumber));
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int number;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "tournament_id")
    private TournamentWithProgression tournament;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tour")
    private List<Battle> battles = new ArrayList<>();

    @Transient
    private Random randomGenerator;

    public void setTournamentByOneSide(TournamentWithProgression tournamentByOneSide){
        this.tournament = tournamentByOneSide;
    }

    public void setTournament(TournamentWithProgression tournament){
        this.tournament = tournament;
        tournament.addTourByOneSide(this);
    }

    public void addBattle(Battle... battles){
        Collections.addAll(this.battles, battles);
        Arrays.stream(battles).forEach(battle -> {
            battle.setTourByOneSide(this);
        });
    }

    public void addBattleByOneSide(Battle... battles){
        Collections.addAll(this.battles, battles);
    }
}
