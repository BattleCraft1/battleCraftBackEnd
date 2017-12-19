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

    public void addParticipants(List<List<Player>> participantsGroups){
        this.participation.addAll(participantsGroups.stream()
                .map(participantsGroup -> {
                    Long groupNumber = this.getNoExistingGroupNumber();
                    Player participant1 = participantsGroup.get(0);
                    Participation participation1 =
                            new Participation(participant1, this,groupNumber);
                    participant1.addParticipationByOneSide(participation1);

                    if(participantsGroup.size()==2){
                        Player participant2 = participantsGroup.get(1);
                        Participation participation2 =
                                new Participation(participant2, this,groupNumber);
                        participant2.addParticipationByOneSide(participation2);
                        return Arrays.asList(participation1,participation2);
                    }

                    return Collections.singletonList(participation1);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    public void editParticipants(List<List<Player>> participantsGroups) {
        List<PlayerWithGroupNumber> participantsWithGroupsNumbers = this.giveGroupNumbersForParticipants(participantsGroups);
        this.addNewParticipation(participantsWithGroupsNumbers);
        this.modifyExistingParticipation(participantsWithGroupsNumbers);
        this.removeNotExistingParticipation(participantsWithGroupsNumbers);
    }

    private void addNewParticipation(List<PlayerWithGroupNumber> participantsWithGroupsNumbers){
        List<Player> existingParticipants = this.participation.stream().map(Participation::getPlayer).collect(Collectors.toList());
        this.participation.addAll(participantsWithGroupsNumbers.stream()
                .filter(participantWithGroupNumber ->
                        !existingParticipants.contains(participantWithGroupNumber.getPlayer()))
                .map(participantWithGroupNumber -> {
                    Player participant = participantWithGroupNumber.getPlayer();
                    Participation participation = new Participation(participant, this,
                                    participantWithGroupNumber.getGroupNumber());
                    participant.addParticipationByOneSide(participation);
                    return participation; })
                .collect(Collectors.toList()));
    }

    private void modifyExistingParticipation(List<PlayerWithGroupNumber> participantsWithGroupsNumbers){
        List<Player> existingParticipants = participantsWithGroupsNumbers.stream()
                .map(PlayerWithGroupNumber::getPlayer).collect(Collectors.toList());
        this.participation.stream().filter(participation ->
                existingParticipants.contains(participation.getPlayer()))
                .forEach(participation -> {
                    Long newGroupNumber = participantsWithGroupsNumbers.stream()
                            .filter(participantWithGroupsNumbers ->
                                    participantWithGroupsNumbers.getPlayer().getName()
                                    .equals(participation.getPlayer().getName()))
                            .findFirst().get().getGroupNumber();
                    if(!participation.getGroupNumber().equals(newGroupNumber))
                        participation.setGroupNumber(newGroupNumber);
                });
    }

    private void removeNotExistingParticipation(List<PlayerWithGroupNumber> participantsWithGroupsNumbers){
        this.participation.removeAll(this.participation.stream()
                .filter(participation -> !participantsWithGroupsNumbers.stream()
                        .map(PlayerWithGroupNumber::getPlayer)
                        .collect(Collectors.toList())
                        .contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
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
                        participationElement2.getGroupNumber().equals(participationElement.getGroupNumber()) &&
                                !participationElement2.equals(participationElement)
                ).findFirst().orElse(null)==null)
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    private List<PlayerWithGroupNumber> giveGroupNumbersForParticipants(List<List<Player>> participantsGroups){
        List<Player> currentParticipants = this.participation.stream()
                .map(Participation::getPlayer).collect(Collectors.toList());

        return participantsGroups.stream()
                .map(participantsGroup -> {
                    Player participant1 = participantsGroup.get(0);

                    Long groupNumber = this.getNoExistingGroupNumber();

                    if (participantsGroup.size() == 2) {
                        Player participant2 = participantsGroup.get(1);

                        if(!currentParticipants.contains(participant1) || !currentParticipants.contains(participant2)){
                            return Arrays.asList(
                                    new PlayerWithGroupNumber(groupNumber,participant1),
                                    new PlayerWithGroupNumber(groupNumber,participant2));
                        }

                        Long oldGroupNumberOfParticipant1 = getOldGroupNumberOfPlayer(participant1);
                        Long oldGroupNumberOfParticipant2 = getOldGroupNumberOfPlayer(participant2);
                        if(!oldGroupNumberOfParticipant1.equals(oldGroupNumberOfParticipant2))
                        {
                            return Arrays.asList(
                                    new PlayerWithGroupNumber(groupNumber,participant1),
                                    new PlayerWithGroupNumber(groupNumber,participant2));
                        }
                        return Arrays.asList(
                                new PlayerWithGroupNumber(oldGroupNumberOfParticipant1,participant1),
                                new PlayerWithGroupNumber(oldGroupNumberOfParticipant2,participant2));
                    }

                    if(currentParticipants.contains(participant1)){
                        Long oldGroupNumberOfParticipant1 = getOldGroupNumberOfPlayer(participant1);
                        return Collections.singletonList(
                                new PlayerWithGroupNumber(oldGroupNumberOfParticipant1, participant1));
                    }

                    return Collections.singletonList(
                            new PlayerWithGroupNumber(groupNumber, participant1));
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
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

    public int getPointsForPlayerFromPreviousTours(Player player){
        return this.getPreviousTurns().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(player))
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
                    participationElement2.getGroupNumber().equals(participationElement.getGroupNumber()) &&
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

    private Long getOldGroupNumberOfPlayer(Player player){
        return  this.participation.stream()
                .filter(participation -> participation.getPlayer().equals(player))
                .findFirst().get().getGroupNumber();
    }


    public void checkIfAllBattlesAreFinished(){
        getActivatedTurns().forEach(Turn::checkIfAllGroupBattlesAreFinished);
    }

    @Getter
    @Setter
    private class PlayerWithGroupNumber{
        private long groupNumber;
        private Player player;

        PlayerWithGroupNumber(long groupNumber,Player player){
            this.groupNumber = groupNumber;
            this.player = player;
        }
    }
}
