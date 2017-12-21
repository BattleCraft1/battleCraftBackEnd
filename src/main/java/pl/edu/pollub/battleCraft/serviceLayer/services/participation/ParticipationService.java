package pl.edu.pollub.battleCraft.serviceLayer.services.participation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.nullObjectPattern.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.GroupTournamentParticipationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.ParticipationDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParticipationService {

    public void confirmParticipation(Player player, List<ParticipationDTO> participationDTOs) {
        List<Tournament> participatedTournaments = participationDTOs.stream()
                .map(ParticipationDTO::getTournament)
                .collect(Collectors.toList());

        this.modifyExistingParticipation(player, participatedTournaments, participationDTOs);
        this.addNewParticipation(player, participatedTournaments, participationDTOs);
        this.removeNotExistingParticipation(player, participatedTournaments);
    }

    private void modifyExistingParticipation(Player player, List<Tournament> participatedTournaments, List<ParticipationDTO> participationDTOs){
        player.getParticipation().stream()
                .filter(participation -> participatedTournaments.contains(participation.getParticipatedTournament()))
                .forEach(participation -> {
                    Tournament tournament = participation.getParticipatedTournament();
                    ParticipationDTO participationDTO = this.findInvitationByTournamentName(tournament.getName(),participationDTOs);
                    participation.setAccepted(participationDTO.isAccepted());

                    if(tournament.getTournamentType() == TournamentType.GROUP){
                        this.sendInvitationToSecondPlayer(player,participation, participationDTO);
                    }
                });
    }

    private void addNewParticipation(Player player, List<Tournament> participatedTournaments, List<ParticipationDTO> participationDTOs){
        player.getParticipation().addAll(
                participatedTournaments.stream()
                .filter(tournament -> !player.getParticipation().stream()
                        .map(Participation::getParticipatedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .filter(tournament -> !tournament.isBanned())
                .map(tournament -> {
                    ParticipationDTO participationDTO = this.findInvitationByTournamentName(tournament.getName(),participationDTOs);

                    ParticipantsGroup participantsGroup = new ParticipantsGroup();

                    Participation participation = this.createNewParticipation(player,tournament,participantsGroup);
                    if(tournament.getTournamentType() == TournamentType.GROUP){
                        this.sendInvitationToSecondPlayer(player,participation, participationDTO);
                    }
                    return participation; })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingParticipation(Player player, List<Tournament> participatedTournaments){
        player.getParticipation().removeAll(player.getParticipation().stream()
                .filter(participation ->
                        !participatedTournaments.contains(participation.getParticipatedTournament()))
                .peek(participation -> {
                    this.deleteParticipation(participation, participation.getParticipatedTournament());
                }).collect(Collectors.toList()));
    }

    private void sendInvitationToSecondPlayer(Player firstPlayer, Participation firstPlayerParticipation, ParticipationDTO participationDTO){
        GroupTournamentParticipationDTO groupParticipationDTO = (GroupTournamentParticipationDTO) participationDTO;

        Player secondPlayer = groupParticipationDTO.getSecondPlayer();
        Tournament tournament = groupParticipationDTO.getTournament();

        if(!(secondPlayer instanceof NullPlayer)){

            Participation secondPlayerParticipation = tournament.getParticipation().stream()
                    .filter(participation -> participation.getPlayer().equals(secondPlayer))
                    .findFirst().orElse(null);

            if(secondPlayerParticipation==null){
                tournament.getParticipation().stream()
                        .filter(participation -> ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(firstPlayerParticipation, participation)
                                && !participation.getPlayer().equals(firstPlayer)).findFirst()
                        .ifPresent(participationWithInThisGroup -> this.deleteParticipation(participationWithInThisGroup, tournament));

                this.createNewParticipation(secondPlayer,tournament,firstPlayerParticipation.getParticipantsGroup());
            }
        }
        else{
            tournament.getParticipation().stream()
                    .filter(participation -> ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(firstPlayerParticipation, participation)
                            && !participation.getPlayer().equals(firstPlayer)).findFirst()
                    .ifPresent(participationWithInThisGroup -> this.deleteParticipation(participationWithInThisGroup,tournament));
        }
    }

    private ParticipationDTO findInvitationByTournamentName(String tournamentName, List<ParticipationDTO> participationDTOs){
        return participationDTOs.stream()
                .filter(invitation -> invitation.getTournament().getName()
                        .equals(tournamentName))
                .findFirst().orElseThrow(() -> new ObjectNotFoundException(Tournament.class,tournamentName));
    }

    private Participation createNewParticipation(Player player, Tournament tournament, ParticipantsGroup participantsGroup){
        Participation participation = new Participation(player, tournament, participantsGroup);
        tournament.addParticipationByOneSide(participation);
        return participation;
    }

    private void deleteParticipation(Participation participation, Tournament tournament){
        tournament.deleteParticipationByOneSide(participation);
        participation.setPlayer(null);
        participation.setParticipatedTournament(null);
        participation.setParticipantsGroup(null);
    }
}
