package pl.edu.pollub.battleCraft.dataLayer.entities.Tour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.subClasses.tournamentWithProgression.TournamentWithProgression;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Play;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour.BattleOnTableNotFinishedYet;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.TournamentPrograssion.prepareFirstTour.BattleWithTableNumberNotFound;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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
        int battlesCount = createNumberOfBattles(tournamentWithProgression.getParticipants().size());
        for(int battleNumber=0;battleNumber<battlesCount;battleNumber++){
            battles.add(new Battle(this,battleNumber));
        }
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
    private Random randomGenerator = new Random();

    @JsonIgnore
    private int createNumberOfBattles(int number){
        if(number%2==0){
            return number/2;
        }
        else{
            return this.createNumberOfBattlesWithOneBattlesWithAlonePlayer(number);
        }
    }

    @JsonIgnore
    private int createNumberOfBattlesWithOneBattlesWithAlonePlayer(int number){
        return number/2+1;
    }

    public void randomizePlayersOnTableInFirstTour(int tableNumber, List<Player> playersWithoutBattle) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        int firstPlayerRandomIndex = randomGenerator.nextInt(playersWithoutBattle.size());
        Player firstPlayer = playersWithoutBattle.get(firstPlayerRandomIndex);
        playersWithoutBattle.remove(firstPlayerRandomIndex);
        int secondPlayerRandomIndex = randomGenerator.nextInt(playersWithoutBattle.size());
        Player secondPlayer = playersWithoutBattle.get(secondPlayerRandomIndex);
        battleWithTableNumber.addPlayers(firstPlayer,secondPlayer);
    }

    public void setPlayersOnTable(int tableNumber, Player firstPlayer, Player secondPlayer) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.addPlayers(firstPlayer,secondPlayer);
    }

    public void setPointsOnTable(int tableNumber, int pointsForFirstPlayer){
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.setPoints(pointsForFirstPlayer);
    }

    @JsonIgnore
    private Battle findBattleByTableNumber(int tableNumber){
        return  this.battles.stream()
                .filter(battle -> battle.getTableNumber()==tableNumber)
                .findFirst().orElseThrow(() -> new BattleWithTableNumberNotFound(tableNumber));
    }

    public void checkIfAllBattlesAreFinished() {
        battles.forEach(
                battle -> {
                    if(this.checkIfPlayerIsAloneInBattle(battle))
                        battle.addAlonePlayer(tournament.getAloneInBattlePlayer());
                    else if(checkIfBattleIsNotFinished(battle))
                        throw new BattleOnTableNotFinishedYet(battle.getTableNumber());
                }
        );
    }

    @JsonIgnore
    private boolean checkIfBattleIsNotFinished(Battle battle){
        return battle.getPlayers().size()==0 ||
                battle.getFirstPlay().getPoints()+battle.getSecondPlay().getPoints()!=20;
    }

    @JsonIgnore
    private boolean checkIfPlayerIsAloneInBattle(Battle battle){
        return battles.indexOf(battle)==battles.size()-1 && battle.getPlayers().size()==1;
    }

    @JsonIgnore
    public List<Player> getAllPlayersInTour(){
        return this.getBattles().stream()
                .flatMap(battle -> battle.getPlayers().stream())
                .map(Play::getPlayer).collect(Collectors.toList());
    }

    public void setTournamentByOneSide(TournamentWithProgression tournamentByOneSide){
        this.tournament = tournamentByOneSide;
    }

    public void setTournament(TournamentWithProgression tournament){
        this.tournament = tournament;
        tournament.addTourByOneSide(this);
    }

    public void addBattle(Battle... battles){
        Collections.addAll(this.battles, battles);
        Arrays.stream(battles).forEach(battle -> battle.setTourByOneSide(this));
    }

    public void addBattleByOneSide(Battle... battles){
        Collections.addAll(this.battles, battles);
    }
}
