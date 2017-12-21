package pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.subClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Turn.Turn;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.nullObjectPattern.NullParticipation;

import javax.persistence.Entity;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class GroupTournament extends Tournament{

    public GroupTournament(){
        super(4);
    }

    public void filterNoAcceptedParticipation(){
        this.participation.removeAll(this.participation.stream()
                .filter(participationElement -> !participationElement.isAccepted())
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));

        this.participation.removeAll(this.participation.stream().filter(participationElement ->
                this.participation.stream().filter(participationElement2 ->
                        ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(participationElement2,participationElement) &&
                                !participationElement2.equals(participationElement)
                ).findFirst().orElse(new NullParticipation()) instanceof NullParticipation)
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    public List<List<Player>> sortPlayersByPointsFromPreviousTours(){
        List<List<Player>> groupedPlayers = this.getGroupedPlayers();
        return groupedPlayers.stream()
                .sorted(Comparator.comparingInt(this::getPointsForPlayerFromPreviousTours))
                .collect(Collectors.toList());
    }

    public void pairPlayersInNextTour(List<List<Player>> playersGroups){
        int tableNumber = 0;
        Iterator<List<Player>> iterator = playersGroups.iterator();
        while (iterator.hasNext()){
            List<Player> firstPlayersGroup = iterator.next();
            iterator.remove();
            while (iterator.hasNext()){
                List<Player> secondPlayersGroup = iterator.next();
                if (!checkIfPlayersPlayedBattle(firstPlayersGroup, secondPlayersGroup) || !iterator.hasNext()){
                    this.getTurnByNumber(this.getCurrentTurnNumber()).setGroupOfPlayersOnTable(tableNumber, firstPlayersGroup, secondPlayersGroup);
                    iterator.remove();
                    iterator = playersGroups.iterator();
                    break;
                }
            }
            tableNumber++;
        }
    }

    private int getPointsForPlayerFromPreviousTours(List<Player> players){
        return this.getPreviousTurns().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(players.get(0)))
                .mapToInt(Play::getPoints).sum();
    }

    private boolean checkIfPlayersPlayedBattle(List<Player> firstPlayer, List<Player> secondPlayer){
        return this.getPreviousTurns().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .filter(battle -> battle.getPlayers().stream()
                        .map(Play::getPlayer)
                        .collect(Collectors.toList())
                        .containsAll(Arrays.asList(firstPlayer.get(0),firstPlayer.get(1),secondPlayer.get(0),secondPlayer.get(1))))
                .collect(Collectors.toList()).size()>0;
    }

    public List<List<Player>> getGroupedPlayers(){
        return this.pairPlayers(this.participation);
    }

    private List<List<Player>> pairPlayers(List<Participation> participation){
        List<List<Player>> playersGroups = new ArrayList<>();
        List<String> includedPlayersNames = new ArrayList<>();

        for(Participation participationElement:participation){
            String playerName = participationElement.getPlayer().getName();
            if(includedPlayersNames.contains(playerName))
                continue;

            List<Player> playersGroup = new ArrayList<>();
            playersGroup.add(participationElement.getPlayer());
            includedPlayersNames.add(participationElement.getPlayer().getName());

            participation.stream().filter(participationElement2 ->
                            ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(participationElement2,participationElement) &&
                            !includedPlayersNames.contains(participationElement2.getPlayer().getName()))
                    .findFirst()
                    .ifPresent(participationElement2 -> {
                        String player2Name = participationElement2.getPlayer().getName();
                        playersGroup.add(participationElement2.getPlayer());
                        includedPlayersNames.add(player2Name);
                    });
            playersGroups.add(playersGroup);
        }

        return playersGroups;
    }

    public List<List<Player>> getPlayersWithoutBattleInTour(int tourNumber){
        List<Participation> participationOfPlayersWithoutBattle = this.getParticipation();
        List<Player> allPlayersWhoHaveBattleInCurrentTour = this.getTurnByNumber(tourNumber).getAllPlayersInTour();
        return this.pairPlayers(participationOfPlayersWithoutBattle.stream()
                .filter(participation1 -> !allPlayersWhoHaveBattleInCurrentTour.contains(participation1.getPlayer()))
                .collect(Collectors.toList()));
    }

    public void checkIfAllBattlesAreFinished(){
        getActivatedTurns().forEach(Turn::checkIfAllGroupBattlesAreFinished);
    }
}
