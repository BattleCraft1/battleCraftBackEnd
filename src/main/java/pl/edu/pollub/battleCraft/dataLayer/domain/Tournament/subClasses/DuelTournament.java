package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Turn.Turn;

import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DuelTournament extends Tournament{

    public DuelTournament(){
        super(2);
    }

    public void addParticipants(List<Player> participants){
        this.participation.addAll(participants.stream()
                .map(participant -> {
                    Participation participation =
                            new Participation(participant, this, this.getNoExistingGroupNumber());
                    participant.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList()));
    }

    public void editParticipants(List<Player> participants) {
        this.addNewParticipation(participants);
        this.removeNotExistingParticipation(participants);
    }

    private void addNewParticipation(List<Player> participants){
        this.participation.addAll(participants.stream()
                .filter(participant -> !this.participation.stream()
                        .map(Participation::getPlayer)
                        .collect(Collectors.toList()).contains(participant))
                .map(participant -> {
                    Participation participation =
                            new Participation(participant, this, this.getNoExistingGroupNumber());
                    participant.addParticipationByOneSide(participation);
                    return participation; })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingParticipation(List<Player> participants){
        this.participation.removeAll(this.participation.stream()
                .filter(participation -> !participants.contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    public void filterNoAcceptedParticipation(){
        this.participation.removeAll(this.participation.stream()
                .filter(participation -> !participation.isAccepted())
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    public List<Player> sortPlayersByPointsFromPreviousTours(){
        List<Player> playersOfTournament = this.getParticipation().stream()
                .map(Participation::getPlayer)
                .collect(Collectors.toList());
        return playersOfTournament.stream()
                .sorted(Comparator.comparingInt(player -> -this.getPointsForPlayerFromPreviousTours(player)))
                .collect(Collectors.toList());
    }

    private int getPointsForPlayerFromPreviousTours(Player player){
        return this.getPreviousTurns().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(player))
                .mapToInt(Play::getPoints).sum();
    }

    public void pairPlayersInNextTour(List<Player> players){
        int tableNumber = 0;
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()){
            Player firstPlayer = iterator.next();
            iterator.remove();
            while (iterator.hasNext()){
                Player secondPlayer = iterator.next();
                if (!checkIfPlayersPlayedBattle(firstPlayer, secondPlayer) || !iterator.hasNext()){
                    this.getTurnByNumber(getCurrentTurnNumber()).setPlayersOnTable(tableNumber, firstPlayer, secondPlayer);
                    iterator.remove();
                    iterator = players.iterator();
                    break;
                }
            }
            tableNumber++;
        }
    }

    public List<Player> getPlayersWithoutBattleInTour(int tourNumber){
        List<Player> playersWithoutBattle = this.getParticipation().stream().map(Participation::getPlayer).collect(Collectors.toList());
        List<Player> allPlayersWhoHaveBattleInCurrentTour = this.getTurnByNumber(tourNumber).getAllPlayersInTour();
        playersWithoutBattle.removeAll(allPlayersWhoHaveBattleInCurrentTour);
        return playersWithoutBattle;
    }

    public void checkIfAllBattlesAreFinished(){
        getActivatedTurns().forEach(Turn::checkIfAllBattlesAreFinished);
    }

    private boolean checkIfPlayersPlayedBattle(Player firstPlayer, Player secondPlayer){
        return this.getPreviousTurns().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .filter(battle -> battle.getPlayers().stream()
                        .map(Play::getPlayer)
                        .collect(Collectors.toList())
                        .containsAll(Arrays.asList(firstPlayer,secondPlayer)))
                .collect(Collectors.toList()).size()>0;
    }
}
