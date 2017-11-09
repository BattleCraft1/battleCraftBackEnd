package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;

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

    public void editParticipantsWithoutRemoving(List<List<Player>> participantsGroups) {
        List<PlayerWithGroupNumber> participantsWithGroupsNumbers = this.giveGroupNumbersForParticipants(participantsGroups);
        this.addNewParticipation(participantsWithGroupsNumbers);
        this.modifyExistingParticipation(participantsWithGroupsNumbers);
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

    private Long getOldGroupNumberOfPlayer(Player player){
        return  this.participation.stream()
                .filter(participation -> participation.getPlayer().equals(player))
                .findFirst().get().getGroupNumber();
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
