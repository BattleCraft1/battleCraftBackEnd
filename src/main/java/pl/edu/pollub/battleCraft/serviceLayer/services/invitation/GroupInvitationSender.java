package pl.edu.pollub.battleCraft.serviceLayer.services.invitation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.nullObjectPattern.NullParticipation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupInvitationSender {

    public void inviteParticipantsGroupsList(Tournament tournament, List<List<Player>> groupParticipants){
        tournament.getParticipation().addAll(
                groupParticipants.stream()
                        .map(participantsGroup -> {

                            ParticipantsGroup group = new ParticipantsGroup();
                            Player participant1 = participantsGroup.get(0);
                            Participation participation1 = new Participation(participant1, tournament,group);
                            participant1.addParticipationByOneSide(participation1);

                            if(participantsGroup.size()==2){
                                Player participant2 = participantsGroup.get(1);
                                Participation participation2 = new Participation(participant2, tournament,group);
                                participant2.addParticipationByOneSide(participation2);
                                return Arrays.asList(participation1,participation2);
                            }

                            return Collections.singletonList(participation1);
                        })
                        .flatMap(List::stream)
                        .collect(Collectors.toList()));
    }

    public void inviteEditedParticipantsGroupsList(Tournament tournament, List<List<Player>> groupParticipants){
        List<PlayerWithGroupDTO> participantsWithGroupsNumbers = this.giveGroupNumbersForParticipants(tournament, groupParticipants);
        this.modifyExistingParticipation(tournament,participantsWithGroupsNumbers);
        this.addNewParticipation(tournament,participantsWithGroupsNumbers);
        this.removeNotExistingParticipation(tournament,participantsWithGroupsNumbers);
    }

    private void addNewParticipation(Tournament tournament,List<PlayerWithGroupDTO> participantsWithGroups){
        List<Participation> existingParticipation = tournament.getParticipation();
        List<Player> existingParticipants = existingParticipation.stream().map(Participation::getPlayer).collect(Collectors.toList());

        existingParticipation.addAll(
                participantsWithGroups.stream()
                        .filter(participantWithGroupNumber -> !existingParticipants.contains(participantWithGroupNumber.getPlayer()))
                        .map(participantWithGroupNumber -> {
                            Player participant = participantWithGroupNumber.getPlayer();
                            Participation participation = new Participation(participant, tournament, participantWithGroupNumber.getParticipantsGroup());
                            participant.addParticipationByOneSide(participation);
                            return participation; })
                        .collect(Collectors.toList()));
    }

    private void modifyExistingParticipation(Tournament tournament, List<PlayerWithGroupDTO> participantsWithGroups){
        List<Participation> existingParticipation = tournament.getParticipation();
        List<Player> newParticipants = participantsWithGroups.stream().map(PlayerWithGroupDTO::getPlayer).collect(Collectors.toList());

        existingParticipation.stream().filter(participation -> newParticipants.contains(participation.getPlayer()))
                .forEach(participation -> {
                    ParticipantsGroup newGroup = participantsWithGroups.stream()
                            .filter(participantWithGroups -> participantWithGroups.getPlayer().getName().equals(participation.getPlayer().getName()))
                            .findFirst().get().getParticipantsGroup();
                    if(!participation.getParticipantsGroup().getId().equals(newGroup.getId()))
                        participation.setParticipantsGroup(newGroup);
                });
    }

    private void removeNotExistingParticipation(Tournament tournament, List<PlayerWithGroupDTO> participantsWithGroups){
        List<Participation> existingParticipation = tournament.getParticipation();

        existingParticipation.removeAll(
                existingParticipation.stream()
                .filter(participation -> !participantsWithGroups.stream()
                        .map(PlayerWithGroupDTO::getPlayer)
                        .collect(Collectors.toList())
                        .contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                    participation.setParticipantsGroup(null);
                }).collect(Collectors.toList()));

    }

    private List<PlayerWithGroupDTO> giveGroupNumbersForParticipants(Tournament tournament, List<List<Player>> participantsGroups){
        List<Player> currentParticipants = tournament.getParticipation().stream().map(Participation::getPlayer).collect(Collectors.toList());

        return participantsGroups.stream()
                .map(participantsGroup -> {
                    Player participant1 = participantsGroup.get(0);

                    ParticipantsGroup newGroup = new ParticipantsGroup();

                    if (participantsGroup.size() == 2) {
                        Player participant2 = participantsGroup.get(1);

                        if(!currentParticipants.contains(participant1) || !currentParticipants.contains(participant2)){
                            return Arrays.asList(new PlayerWithGroupDTO(participant1,newGroup), new PlayerWithGroupDTO(participant2,newGroup));
                        }

                        ParticipantsGroup oldGroupOfParticipant1 = getOldGroupNumberOfPlayer(tournament,participant1);
                        ParticipantsGroup oldGroupOfParticipant2 = getOldGroupNumberOfPlayer(tournament,participant2);
                        if(!oldGroupOfParticipant1.getId().equals(oldGroupOfParticipant2.getId()))
                        {
                            return Arrays.asList(new PlayerWithGroupDTO(participant1,newGroup), new PlayerWithGroupDTO(participant2,newGroup));
                        }
                        return Arrays.asList(new PlayerWithGroupDTO(participant1,oldGroupOfParticipant1), new PlayerWithGroupDTO(participant2,oldGroupOfParticipant2));
                    }

                    if(currentParticipants.contains(participant1)){
                        ParticipantsGroup oldGroupOfParticipant1 = getOldGroupNumberOfPlayer(tournament,participant1);
                        return Collections.singletonList(new PlayerWithGroupDTO(participant1,oldGroupOfParticipant1));
                    }

                    return Collections.singletonList(new PlayerWithGroupDTO(participant1,newGroup));
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private ParticipantsGroup getOldGroupNumberOfPlayer(Tournament tournament,Player player){
        return  tournament.getParticipation().stream()
                .filter(participation -> participation.getPlayer().equals(player))
                .findFirst().orElse(new NullParticipation()).getParticipantsGroup();
    }
}
