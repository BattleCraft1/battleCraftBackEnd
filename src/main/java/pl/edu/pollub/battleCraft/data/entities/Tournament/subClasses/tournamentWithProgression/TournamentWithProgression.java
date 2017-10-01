package pl.edu.pollub.battleCraft.data.entities.Tournament.subClasses.tournamentWithProgression;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.data.entities.Tour.Tour;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.finish.TournamentCannotBeFinished;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour.NotValidPointsNumber;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour.ThisTournamentIsNotInProgress;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareEveryNextTour.TournamentIsFinished;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.prepareFirstTour.ThisPlayerHaveBattleInCurrentTour;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentPrograssion.start.TooManyToursInTournament;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class TournamentWithProgression extends Tournament{

    public TournamentWithProgression(Tournament tournament,int toursCount) {
        super();

        int playersNumber = tournament.getParticipants().size();
        int maxToursNumber = factorial(playersNumber-1)/factorial(playersNumber-2);
        if(toursCount>maxToursNumber)
            throw new TooManyToursInTournament(maxToursNumber);

        this.setStatus(TournamentStatus.IN_PROGRESS);
        this.setBanned(false);
        this.changeAddress(tournament.getAddress());
        this.setName(tournament.getName());
        this.setTablesCount(tournament.getTablesCount());
        this.initMaxPlayers(tournament.getMaxPlayers());
        this.setDateOfEnd(tournament.getDateOfEnd());
        this.setDateOfStart(new Date());
        this.chooseGame(tournament.getGame());
        this.setTournamentClass(tournament.getTournamentClass());
        this.addParticipants(tournament.getParticipants().stream().map(Participation::getPlayer).toArray(Player[]::new));
        this.addOrganizers(tournament.getOrganizers().stream().map(Organization::getOrganizer).toArray(Organizer[]::new));

        for(int toursNumber=0;toursNumber<toursCount;toursNumber++){
            new Tour(toursNumber,this);
        }
        this.currentTour = this.tours.get(0);
    }

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Tour> tours = new ArrayList<>();

    @Transient
    private Tour currentTour;

    public void setRandomPlayersOnTableInFirstTour(int tableNumber){
        List<Player> playersWithoutBattle = this.getPlayersWithoutBattle();
        currentTour.randomizePlayersOnTableInFirstTour(tableNumber,playersWithoutBattle);
    }

    public void setPlayersOnTableInFirstTour(int tableNumber, Player firstPlayer, Player secondPlayer){
        List<Player> playersWithoutBattle = this.getPlayersWithoutBattle();
        if(!playersWithoutBattle.contains(secondPlayer))
            throw new ThisPlayerHaveBattleInCurrentTour(secondPlayer.getName());
        else if(!playersWithoutBattle.contains(firstPlayer))
            throw new ThisPlayerHaveBattleInCurrentTour(firstPlayer.getName());

        currentTour.setPlayersOnTable(tableNumber,firstPlayer,secondPlayer);
    }

    @JsonIgnore
    public List<Player> getPlayersWithoutBattle(){
        List<Player> playersWithoutBattle = this.getAllPlayersOfTournament();
        List<Player> allPlayersWhoHaveBattleInCurrentTour = this.currentTour.getAllPlayersInTour();
        playersWithoutBattle.removeAll(allPlayersWhoHaveBattleInCurrentTour);
        return playersWithoutBattle;
    }

    @JsonIgnore
    public List<Player> getAllPlayersOfTournament(){
        return this.getParticipants().stream().map(Participation::getPlayer).collect(Collectors.toList());
    }

    public void prepareNextTour(){
        if(this.getStatus()==TournamentStatus.IN_PROGRESS) {
            currentTour.checkIfAllBattlesAreFinished();
            int indexOfCurrentTour = this.getTours().indexOf(currentTour);
            int indexOfNextTour = indexOfCurrentTour + 1;
            if (indexOfNextTour == this.getTours().size())
                throw new TournamentIsFinished(this.getName());
            else {
                List<Player> playersSortedByPointsFromPreviousTours = this.sortPlayersByPointsFromPreviousTours();
                currentTour = this.getTours().get(indexOfNextTour);
                this.pairPlayersInNextTour(playersSortedByPointsFromPreviousTours);
            }
        }
        else
            throw new ThisTournamentIsNotInProgress(this.getName());
    }

    private void pairPlayersInNextTour(List<Player> players){
        int tableNumber = 0;
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()){
            Player firstPlayer = iterator.next();
            iterator.remove();
            while (iterator.hasNext()){
                Player secondPlayer = iterator.next();
                if (!checkIfPlayersPlayedBattle(firstPlayer, secondPlayer)){
                    currentTour.setPlayersOnTable(tableNumber, firstPlayer, secondPlayer);
                    iterator.remove();
                    break;
                }
            }
            tableNumber++;
        }
    }

    @JsonIgnore
    private boolean checkIfPlayersPlayedBattle(Player firstPlayer, Player secondPlayer){
        return this.getPreviousTours().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .filter(battle -> battle.getPlayers().stream()
                        .map(Play::getPlayer)
                        .collect(Collectors.toList())
                        .containsAll(Arrays.asList(firstPlayer,secondPlayer)))
                .collect(Collectors.toList()).size()>0;
    }

    @JsonIgnore
    private List<Player> sortPlayersByPointsFromPreviousTours(){
        List<Player> playersOfTournament = this.getParticipants().stream()
                .map(Participation::getPlayer)
                .collect(Collectors.toList());
        return playersOfTournament.stream()
                .sorted(Comparator.comparingInt(this::getPointsForPlayerFromPreviousTours))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    private int getPointsForPlayerFromPreviousTours(Player player){
        return this.getPreviousTours().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(player))
                .mapToInt(Play::getPoints).sum();
    }

    @JsonIgnore
    private List<Tour> getPreviousTours(){
        return this.getTours().subList(0,this.getTours().indexOf(currentTour));
    }

    public void setPointsOnTable(int tableNumber, int pointsForFirstPlayer){
        if(pointsForFirstPlayer>20 || pointsForFirstPlayer<0)
            throw new NotValidPointsNumber();
        currentTour.setPointsOnTable(tableNumber, pointsForFirstPlayer);
    }

    public void finishTournament(){
        int indexOfCurrentTour = this.getTours().indexOf(currentTour);
        int indexOfNextTour = indexOfCurrentTour + 1;
        if (indexOfNextTour != this.getTours().size())
            throw new TournamentCannotBeFinished();

        this.setStatus(TournamentStatus.FINISHED);
    }

    @JsonIgnore
    public Player getAloneInBattlePlayer() {
        return this.getPlayersWithoutBattle().get(0);
    }

    public void addTour(Tour tour){
        this.tours.add(tour);
        tour.setTournamentByOneSide(this);
    }

    public void addTourByOneSide(Tour tour){
        this.tours.add(tour);
    }

    private static int factorial(int number) {
        if (number <= 1) return 1;
        else return number * factorial(number - 1);
    }
}
