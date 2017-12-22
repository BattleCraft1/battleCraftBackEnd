package pl.edu.pollub.battleCraft.serviceLayer.services.invitation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.Participation;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationToParticipationSender {

    public void inviteParticipantsList(Tournament tournament, List<Player> participants){
        List<Participation> currentParticipation = tournament.getParticipation();

        currentParticipation.addAll(participants.stream()
                .map(participant -> {
                    Participation participation = new Participation(participant, tournament);
                    participant.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList()));
    }

    public void inviteEditedParticipantsList(Tournament tournament, List<Player> participants){
        this.addNewParticipation(tournament, participants);
        this.removeNotExistingParticipation(tournament, participants);
    }

    private void addNewParticipation(Tournament tournament, List<Player> participants){
        List<Participation> currentParticipation = tournament.getParticipation();

        currentParticipation.addAll(participants.stream()
                .filter(participant -> !currentParticipation.stream()
                        .map(Participation::getPlayer)
                        .collect(Collectors.toList()).contains(participant))
                .map(participant -> {
                    Participation newParticipation = new Participation(participant, tournament);
                    participant.addParticipationByOneSide(newParticipation);
                    return newParticipation;
                })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingParticipation(Tournament tournament ,List<Player> participants){
        List<Participation> currentParticipation = tournament.getParticipation();

        currentParticipation.removeAll(currentParticipation.stream()
                .filter(participation -> !participants.contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                    participation.setParticipantsGroup(null);
                }).collect(Collectors.toList()));
    }

}
