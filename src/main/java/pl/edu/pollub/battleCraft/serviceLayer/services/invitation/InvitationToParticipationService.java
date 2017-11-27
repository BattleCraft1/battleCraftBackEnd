package pl.edu.pollub.battleCraft.serviceLayer.services.invitation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.nullObjectPattern.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.GroupTournamentInvitationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.InvitationDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationToParticipationService {

    public void sendInvitations(Player player, List<InvitationDTO> invitationDTOS) {
        List<Tournament> participatedTournaments = invitationDTOS.stream()
                .map(InvitationDTO::getTournament)
                .collect(Collectors.toList());

        this.modifyExistingParticipation(player, participatedTournaments, invitationDTOS);
        this.addNewParticipation(player, participatedTournaments, invitationDTOS);
        this.removeNotExistingParticipation(player, participatedTournaments);
    }

    private void modifyExistingParticipation(Player player, List<Tournament> participatedTournaments, List<InvitationDTO> invitationDTOS){
        player.getParticipation().stream()
                .filter(participation -> participatedTournaments.contains(participation.getParticipatedTournament()))
                .forEach(participation -> {
                    Tournament tournament = participation.getParticipatedTournament();
                    InvitationDTO invitationDTO = this.findInvitationByTournamentName(tournament.getName(),invitationDTOS);
                    participation.setAccepted(invitationDTO.isAccepted());
                    if(tournament.getTournamentType() == TournamentType.GROUP){
                        this.sendInvitationToSecondPlayer(player,participation.getGroupNumber(),
                                (GroupTournamentInvitationDTO) invitationDTO);
                    }
                });
    }

    private void addNewParticipation(Player player, List<Tournament> participatedTournaments, List<InvitationDTO> invitationDTOS){
        player.getParticipation().addAll(participatedTournaments.stream()
                .filter(tournament -> !player.getParticipation().stream()
                        .map(Participation::getParticipatedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .filter(tournament -> !tournament.isBanned())
                .map(tournament -> {
                    InvitationDTO invitationDTO = this.findInvitationByTournamentName(tournament.getName(),invitationDTOS);
                    Participation participation = this.createNewParticipation(player,tournament,tournament.getNoExistingGroupNumber());
                    if(tournament.getTournamentType() == TournamentType.GROUP){
                        this.sendInvitationToSecondPlayer(player,participation.getGroupNumber(),
                                (GroupTournamentInvitationDTO) invitationDTO);
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

    private void sendInvitationToSecondPlayer(Player firstPlayer, Long groupNumber,
                                              GroupTournamentInvitationDTO groupTournamentInvitationDTO){
        Player secondPlayer = groupTournamentInvitationDTO.getSecondPlayer();
        Tournament tournament = groupTournamentInvitationDTO.getTournament();
        if(!(secondPlayer instanceof NullPlayer)){
            Participation secondPlayerParticipation = tournament.getParticipation().stream()
                    .filter(participation -> participation.getPlayer().equals(secondPlayer))
                    .findFirst().orElse(null);
            if(secondPlayerParticipation==null){
                Participation participationWithThisGroupNumber = tournament.getParticipation().stream()
                        .filter(participation -> participation.getGroupNumber().equals(groupNumber) &&
                                !participation.getPlayer().equals(firstPlayer))
                        .findFirst().orElse(null);
                if(participationWithThisGroupNumber!=null){
                    this.deleteParticipation(participationWithThisGroupNumber,tournament);
                }
                this.createNewParticipation(secondPlayer,tournament,groupNumber);
            }
        }
        else{
            Participation participationWithThisGroupNumber = tournament.getParticipation().stream()
                    .filter(participation -> participation.getGroupNumber().equals(groupNumber) &&
                            !participation.getPlayer().equals(firstPlayer))
                    .findFirst().orElse(null);
            if(participationWithThisGroupNumber!=null){
                this.deleteParticipation(participationWithThisGroupNumber,tournament);
            }
        }
    }

    private InvitationDTO findInvitationByTournamentName(String tournamentName, List<InvitationDTO> invitationDTOS){
        return invitationDTOS.stream()
                .filter(invitation -> invitation.getTournament().getName()
                        .equals(tournamentName))
                .findFirst().get();
    }

    private Participation createNewParticipation(Player player, Tournament tournament, Long groupNumber){
        Participation participation = new Participation(player, tournament, groupNumber);
        tournament.addParticipationByOneSide(participation);
        return participation;
    }

    private void deleteParticipation(Participation participation, Tournament tournament){
        tournament.deleteParticipationByOneSide(participation);
        participation.setPlayer(null);
        participation.setParticipatedTournament(null);
    }
}
