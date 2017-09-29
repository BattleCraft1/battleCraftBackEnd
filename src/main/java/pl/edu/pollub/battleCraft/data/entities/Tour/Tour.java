package pl.edu.pollub.battleCraft.data.entities.Tour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.data.entities.Tournament.subClasses.tournamentWithProgression.TournamentWithProgression;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour.BattleOnTableNotFinishedYet;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.start.BattleWithTableNumberNotFound;

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
        int battlesNumber = createNumberOfBattles(tournamentWithProgression.getPlayersNumber());
        for(int i=0;i<battlesNumber;i++){
            battles.add(new Battle(i));
        }
    }

    @JsonIgnore
    @Transient
    private int createNumberOfBattles(int number){
        if(number%2==0){
            return number/2;
        }
        else{
            return this.createNumberOfBattlesWithOneBattlesWithAlonePlayer(number);
        }
    }

    @JsonIgnore
    @Transient
    private int createNumberOfBattlesWithOneBattlesWithAlonePlayer(int number){
        return number/2+1;
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
    public void setPlayersOnTable(int tableNumber, Player firstPlayer, Player secondPlayer) {
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.addPlayers(firstPlayer,secondPlayer);
    }

    @JsonIgnore
    @Transient
    public void setPointsOnTable(int tableNumber, int pointsForFirstPlayer){
        Battle battleWithTableNumber = this.findBattleByTableNumber(tableNumber);
        battleWithTableNumber.setPoints(pointsForFirstPlayer);
    }

    @JsonIgnore
    @Transient
    private Battle findBattleByTableNumber(int tableNumber){
        return  this.battles.stream()
                .filter(battle -> battle.getTableNumber()==tableNumber)
                .findFirst().orElseThrow(() -> new BattleWithTableNumberNotFound(tableNumber));
    }

    @JsonIgnore
    @Transient
    public void checkIfAllBattlesAreFinished() {
        if(battles.size() % 2 == 0)
            battles.forEach(
                    battle -> {
                        if(checkIfBattleIsFinished(battle))
                            throw new BattleOnTableNotFinishedYet(battle.getTableNumber());
                    }
            );
        else
            battles.forEach(
                    battle -> {
                        if(this.checkIfPlayerIsAloneInBattle(battle))
                            battle.addAlonePlayer(tournament.getAlonePlayer());
                        else if(checkIfBattleIsFinished(battle))
                            throw new BattleOnTableNotFinishedYet(battle.getTableNumber());
                    }
            );
    }

    @JsonIgnore
    @Transient
    private boolean checkIfBattleIsFinished(Battle battle){
        return battle.getPlayers().size()==0 ||
                battle.getFirstPlay().getPoints()+battle.getSecondPlay().getPoints()!=20;
    }

    @JsonIgnore
    @Transient
    private boolean checkIfPlayerIsAloneInBattle(Battle battle){
        return battles.indexOf(battle)==battles.size()-1 && battle.getPlayers().size()==1;
    }

    @JsonIgnore
    @Transient
    public void setBattlesOnTablesByPointsNumbersFromPreviousTour(Tour previousTour) {
        List<Play> previousTourPlays = previousTour.getBattles().stream()
                .flatMap(battle -> battle.getPlayers().stream())
                .sorted(Comparator.comparingInt(Play::getPoints))
                .collect(Collectors.toList());
        int numberOfBattles = this.createNumberOfBattles(previousTourPlays.size());
        for(int i=0;i<numberOfBattles;i++){
            battles.add(new Battle(i));
            if(i!=numberOfBattles-1 || numberOfBattles % 2 != 0){
                this.setPlayersOnTable(
                        i,
                        previousTourPlays.get(i*2).getPlayer(),
                        previousTourPlays.get(i*2+1).getPlayer()
                );
            }
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
        Arrays.stream(battles).forEach(battle -> battle.setTourByOneSide(this));
    }

    public void addBattleByOneSide(Battle... battles){
        Collections.addAll(this.battles, battles);
    }
}
