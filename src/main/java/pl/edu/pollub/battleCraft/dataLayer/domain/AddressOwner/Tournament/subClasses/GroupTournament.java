package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.group.ParticipationGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;

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
                    ParticipationGroup group = new ParticipationGroup();
                    Player participant1 = participantsGroup.get(0);
                    Participation participation1 = new Participation(participant1, this,group);
                    participant1.addParticipationByOneSide(participation1);

                    if(participantsGroup.size()==2){
                        Player participant2 = participantsGroup.get(1);
                        Participation participation2 = new Participation(participant2, this,group);
                        participant2.addParticipationByOneSide(participation2);
                        return Arrays.asList(participation1,participation2);
                    }

                    return Collections.singletonList(participation1);
                })
                .flatMap(List::stream)
                .collect(Collectors.toList()));
    }

    public void editParticipants(List<List<Player>> participantsGroups) {
        List<PlayerWithGroup> participantsWithGroupsNumbers = this.groupParticipants(participantsGroups);
        this.addNewParticipation(participantsWithGroupsNumbers);
        this.modifyExistingParticipation(participantsWithGroupsNumbers);
        this.removeNotExistingParticipation(participantsWithGroupsNumbers);
    }

    public void filterNoAcceptedParticipation(){
        this.participation.removeAll(this.participation.stream()
                .filter(participationElement -> !participationElement.isAccepted())
                .peek(this::removeParticipation).collect(Collectors.toList()));

        this.participation.removeAll(this.participation.stream().filter(participationElement ->
                this.participation.stream().filter(participationElement2 ->
                        participationElement2.getParticipationGroup().getId().equals(participationElement.getParticipationGroup().getId()) &&
                                !participationElement2.equals(participationElement))
                        .findFirst().orElse(null)==null).peek(this::removeParticipation).collect(Collectors.toList()));
    }

    public void checkIfAllBattlesAreFinished(){
        getActivatedTours().forEach(Tour::checkIfAllGroupBattlesAreFinished);
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
                    this.getTourByNumber(this.getCurrentTourNumber()).setGroupOfPlayersOnTable(tableNumber, firstPlayersGroup, secondPlayersGroup);
                    iterator.remove();
                    iterator = playersGroups.iterator();
                    break;
                }
            }
            tableNumber++;
        }
    }

    public List<List<Player>> sortPlayersByPointsFromPreviousTours(){
        List<List<Player>> groupedPlayers = this.getGroupedPlayers();
        return groupedPlayers.stream()
                .sorted(Comparator.comparingInt(this::getPointsForPlayerFromPreviousTours))
                .collect(Collectors.toList());
    }

    public int getPointsForPlayerFromPreviousTours(Player player){
        return this.getPreviousTours().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(player))
                .mapToInt(Play::getPoints).sum();
    }

    public List<List<Player>> getGroupedPlayers(){
        return this.pairPlayers(this.participation);
    }

    private void addNewParticipation(List<PlayerWithGroup> participantsWithGroups){
        List<Player> existingParticipants = this.participation.stream().map(Participation::getPlayer).collect(Collectors.toList());
        this.participation.addAll(participantsWithGroups.stream()
                .filter(participantWithGroup ->
                        !existingParticipants.contains(participantWithGroup.getPlayer()))
                .map(participantWithGroup -> {
                    Player participant = participantWithGroup.getPlayer();
                    Participation participation = new Participation(participant, this, participantWithGroup.getGroup());
                    participant.addParticipationByOneSide(participation);
                    return participation; })
                .collect(Collectors.toList()));
    }

    private void modifyExistingParticipation(List<PlayerWithGroup> participantsWithGroups){
        List<Player> existingParticipants = participantsWithGroups.stream().map(PlayerWithGroup::getPlayer).collect(Collectors.toList());
        this.participation.stream().filter(participation ->
                existingParticipants.contains(participation.getPlayer()))
                .forEach(participation -> {
                    ParticipationGroup newGroup = participantsWithGroups.stream()
                            .filter(participantWithGroups -> participantWithGroups.getPlayer().getName().equals(participation.getPlayer().getName()))
                            .findFirst().orElseThrow(() -> new ObjectNotFoundException(Participation.class)).getGroup();
                    if(!participation.getParticipationGroup().getId().equals(newGroup.getId()))
                        participation.setParticipationGroup(newGroup);
                });
    }

    private void removeNotExistingParticipation(List<PlayerWithGroup> participantsWithGroups){
        this.participation.removeAll(this.participation.stream()
                .filter(participation -> !participantsWithGroups.stream()
                        .map(PlayerWithGroup::getPlayer)
                        .collect(Collectors.toList())
                        .contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.removeGroup();
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    private List<PlayerWithGroup> groupParticipants(List<List<Player>> participantsGroups){
        List<Player> currentParticipants = this.participation.stream()
                .map(Participation::getPlayer).collect(Collectors.toList());

        return participantsGroups.stream()
                .map(participantsGroup -> {
                    Player participant1 = participantsGroup.get(0);

                    ParticipationGroup newGroup = new ParticipationGroup();

                    if (participantsGroup.size() == 2) {
                        Player participant2 = participantsGroup.get(1);

                        if(!currentParticipants.contains(participant1) || !currentParticipants.contains(participant2)){
                            return Arrays.asList(
                                    new PlayerWithGroup(newGroup,participant1),
                                    new PlayerWithGroup(newGroup,participant2));
                        }

                        ParticipationGroup previousGroupForParticipation1 = getPreviousGroupForPlayer(participant1);
                        ParticipationGroup previousGroupForParticipation2 = getPreviousGroupForPlayer(participant2);
                        if(!previousGroupForParticipation1.getId().equals(previousGroupForParticipation2.getId()))
                        {
                            return Arrays.asList(
                                    new PlayerWithGroup(newGroup,participant1),
                                    new PlayerWithGroup(newGroup,participant2));
                        }
                        return Arrays.asList(
                                new PlayerWithGroup(previousGroupForParticipation1,participant1),
                                new PlayerWithGroup(previousGroupForParticipation2,participant2));
                    }

                    if(currentParticipants.contains(participant1)){
                        ParticipationGroup previousGroupForParticipation1 = getPreviousGroupForPlayer(participant1);
                        return Collections.singletonList(
                                new PlayerWithGroup(previousGroupForParticipation1, participant1));
                    }

                    return Collections.singletonList(
                            new PlayerWithGroup(newGroup, participant1));
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private int getPointsForPlayerFromPreviousTours(List<Player> players){
        return this.getPreviousTours().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .flatMap(battle -> battle.getPlayers().stream())
                .filter(play -> play.getPlayer().equals(players.get(0)))
                .mapToInt(Play::getPoints).sum();
    }

    private boolean checkIfPlayersPlayedBattle(List<Player> firstPlayer, List<Player> secondPlayer){
        return this.getPreviousTours().stream()
                .flatMap(tour -> tour.getBattles().stream())
                .filter(battle -> battle.getPlayers().stream()
                        .map(Play::getPlayer)
                        .collect(Collectors.toList())
                        .containsAll(Arrays.asList(firstPlayer.get(0),firstPlayer.get(1),secondPlayer.get(0),secondPlayer.get(1))))
                .collect(Collectors.toList()).size()>0;
    }

    private List<List<Player>> pairPlayers(Set<Participation> participation){
        Set<ParticipationGroup> groups = participation.stream().map(Participation::getParticipationGroup).collect(Collectors.toSet());

        return groups.stream().map(group -> group.getParticipationInGroup().stream()
                .map(Participation::getPlayer)
                .collect(Collectors.toList())).collect(Collectors.toList());
    }

    public List<List<Player>> getPlayersWithoutBattleInTour(int tourNumber){
        Set<Participation> participationOfPlayersWithoutBattle = this.getParticipation();
        List<Player> allPlayersWhoHaveBattleInCurrentTour = this.getTourByNumber(tourNumber).getAllPlayersInTour();
        return this.pairPlayers(participationOfPlayersWithoutBattle.stream()
                .filter(participation1 -> !allPlayersWhoHaveBattleInCurrentTour.contains(participation1.getPlayer()))
                .collect(Collectors.toSet()));
    }

    private ParticipationGroup getPreviousGroupForPlayer(Player player){
        return  this.participation.stream()
                .filter(participation -> participation.getPlayer().equals(player))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Participation.class,player.getName())).getParticipationGroup();
    }

    private void removeParticipation(Participation participation){
        participation.getPlayer().deleteParticipationByOneSide(participation);
        participation.setPlayer(null);
        participation.removeGroup();
        participation.setParticipatedTournament(null);
    }

    @Getter
    @Setter
    private class PlayerWithGroup{
        private ParticipationGroup group;
        private Player player;

        PlayerWithGroup(ParticipationGroup group, Player player){
            this.group = group;
            this.player = player;
        }
    }
}
