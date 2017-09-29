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
    public void randomizePlayersOnTable(int tableNumber){
        List<Player> playersWithoutBattle = this.getPlayersWithoutBattle();
        currentTour.randomizePlayersOnTable(tableNumber,playersWithoutBattle);
    }

    @JsonIgnore
    @Transient
    public void setPlayersOnTable(int tableNumber, Player firstPlayer, Player secondPlayer){

    }

    @JsonIgnore
    @Transient
    private List<Player> getPlayersWithoutBattle(){
        List<Player> allPlayersOfTournament = new ArrayList<>();
        allPlayersOfTournament.addAll(this.getParticipants().stream().map(Participation::getPlayer).collect(Collectors.toList()));
        return allPlayersOfTournament.stream().filter(player -> !this.tours.stream()
                                .flatMap(tour -> tour.getBattles().stream())
                                .collect(Collectors.toList()).stream()
                                .flatMap(battle -> battle.getPlays().stream())
                                .collect(Collectors.toList()).stream()
                                .map(Play::getPlayer).collect(Collectors.toList()).contains(player))
                                .collect(Collectors.toList());
    }

    public void addTour(Tour tour){
        this.tours.add(tour);
        tour.setTournamentByOneSide(this);
    }

    public void addTourByOneSide(Tour tour){
        this.tours.add(tour);
    }

}
