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
}
