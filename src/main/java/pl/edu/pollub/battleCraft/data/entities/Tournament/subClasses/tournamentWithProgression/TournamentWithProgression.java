package pl.edu.pollub.battleCraft.data.entities.Tournament.subClasses.tournamentWithProgression;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.data.entities.Tour.Tour;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareFirstTour.ThisPlayerHaveBattleInCurrentTour;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class TournamentWithProgression extends Tournament{

    public TournamentWithProgression(Tournament tournament,int toursNumber) {
        super();
        this.setStatus(TournamentStatus.IN_PROGRESS);
        this.setBanned(false);
        this.setDateOfEnd(tournament.getDateOfEnd());
        this.setDateOfStart(new Date());
        this.setName(tournament.getName());
        this.setTablesCount(tournament.getTablesCount());
        this.setGame(tournament.getGame());
        this.setTournamentClass(tournament.getTournamentClass());
        this.setParticipants(tournament.getParticipants());
        this.setOrganizers(tournament.getOrganizers());
        this.setFreeSlots(tournament.getFreeSlots());
        this.setMaxPlayers(tournament.getMaxPlayers());
        this.setPlayersNumber(tournament.getPlayersNumber());
        this.setFreeSlots(tournament.getFreeSlots());

        for(int i=0;i<toursNumber;i++){
            Tour tour = new Tour(toursNumber,this);
        }
        this.currentTour = tours.get(0);
    }

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Tour> tours = new ArrayList<>();

    @Transient
    private Tour currentTour;


    @JsonIgnore
    @Transient
    public void randomizePlayersOnTableInFirstTour(int tableNumber){
        List<Player> playersWithoutBattle = this.getPlayersWithoutBattle();
        currentTour.randomizePlayersOnTableInFirstTour(tableNumber,playersWithoutBattle);
    }

    @JsonIgnore
    @Transient
    public void setPlayersOnTableInFirstTour(int tableNumber, Player firstPlayer, Player secondPlayer){
        List<Player> playersWithoutBattle = this.getPlayersWithoutBattle();
        if(!playersWithoutBattle.contains(secondPlayer))
            throw new ThisPlayerHaveBattleInCurrentTour(secondPlayer.getName());
        else if(!playersWithoutBattle.contains(firstPlayer))
            throw new ThisPlayerHaveBattleInCurrentTour(firstPlayer.getName());

        currentTour.setPlayersOnTableInFirstTour(tableNumber,firstPlayer,secondPlayer);
    }

    @JsonIgnore
    @Transient
    public void prepareNextTour(){
        currentTour.checkIfAllBattlesAreFinished()


    }

    @JsonIgnore
    @Transient
    public void setPointsOnTable(int tableNumber, int pointsForFirstPlayer){
        currentTour.setPointsOnTable(tableNumber, pointsForFirstPlayer);
    }

    @JsonIgnore
    @Transient
    public List<Player> getPlayersWithoutBattle(){
        List<Player> playersWithoutBattle = new ArrayList<>();
        playersWithoutBattle.addAll(this.getParticipants().stream().map(Participation::getPlayer).collect(Collectors.toList()));
        List<Battle> allBattlesInCurrentTour = this.currentTour.getBattles();
        List<Player> allPlayersWhoHaveBattleInCurrentTour =
                allBattlesInCurrentTour.stream()
                        .flatMap(battle -> battle.getPlayers().stream())
                        .map(Play::getPlayer).collect(Collectors.toList());
        playersWithoutBattle.removeAll(allPlayersWhoHaveBattleInCurrentTour);
        return playersWithoutBattle;
    }

    public void addTour(Tour tour){
        this.tours.add(tour);
        tour.setTournamentByOneSide(this);
    }

    public void addTourByOneSide(Tour tour){
        this.tours.add(tour);
    }

}
