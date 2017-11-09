package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.subClasses.GroupTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.InvitationDTO.GroupTournamentInvitationDTO;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.InvitationDTO.InvitationDTO;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Player extends UserAccount {

    public Player() {
        super(UserType.ACCEPTED);
        this.banned = false;
    }

    public Player(UserType userType) {
        super(userType);
        this.banned = false;
    }

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,  mappedBy = "player")
    private List<Participation> participation = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "player")
    private List<Play> battles = new ArrayList<>();

    private boolean banned;

    public void editParticipation(List<InvitationDTO> invitationDTOS) {

        List<Tournament> participatedTournaments = invitationDTOS.stream()
                .map(InvitationDTO::getTournament)
                .collect(Collectors.toList());

        this.modifyExistingParticipation(participatedTournaments, invitationDTOS);
        this.addNewParticipation(participatedTournaments, invitationDTOS);
        this.removeNotExistingParticipation(participatedTournaments);
    }

    private void modifyExistingParticipation(List<Tournament> participatedTournaments, List<InvitationDTO> invitationDTOS){
        this.participation.stream()
                .filter(participation -> participatedTournaments.contains(participation.getParticipatedTournament()))
                .forEach(participation -> {
                    Tournament tournament = participation.getParticipatedTournament();
                    InvitationDTO invitationDTO = this.findInvitationByTournamentName(tournament.getName(),invitationDTOS);
                    participation.setAccepted(invitationDTO.isAccepted());
                    if(tournament.getTournamentType() == TournamentType.GROUP){
                        Player player = ((GroupTournamentInvitationDTO)invitationDTO).getSecondPlayer();
                        if(!(player instanceof NullPlayer))
                        ((GroupTournament)tournament).editParticipantsWithoutRemoving(Collections.singletonList(Arrays.asList(this,player)));
                    }
                });
    }

    private void addNewParticipation(List<Tournament> participatedTournaments, List<InvitationDTO> invitationDTOS){
        this.participation.addAll(participatedTournaments.stream()
                .filter(tournament -> !this.participation.stream()
                        .map(Participation::getParticipatedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .map(tournament -> {
                    InvitationDTO invitationDTO = this.findInvitationByTournamentName(tournament.getName(),invitationDTOS);
                    Participation participation = new Participation(this, tournament, tournament.getNoExistingGroupNumber());
                    tournament.addParticipationByOneSide(participation);
                    if(tournament.getTournamentType() == TournamentType.GROUP){
                        Player player = ((GroupTournamentInvitationDTO)invitationDTO).getSecondPlayer();
                        if(!(player instanceof NullPlayer))
                        ((GroupTournament)tournament).editParticipantsWithoutRemoving(Collections.singletonList(Arrays.asList(this,player)));
                    }
                    return participation; })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingParticipation(List<Tournament> participatedTournaments){
        this.participation.removeAll(this.participation.stream()
                .filter(participation ->
                        !participatedTournaments.contains(participation.getParticipatedTournament())
                                && participation.getParticipatedTournament().getStatus()==TournamentStatus.ACCEPTED)
                .peek(participation -> {
                    participation.getParticipatedTournament().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    private InvitationDTO findInvitationByTournamentName(String tournamentName, List<InvitationDTO> invitationDTOS){
        return invitationDTOS.stream()
                .filter(invitation -> invitation.getTournament().getName()
                        .equals(tournamentName))
                .findFirst().get();
    }

    public void addParticipationByOneSide(Participation participation) {
        this.deleteParticipationWithTheSameTournamentName(participation.getParticipatedTournament().getName());
        this.participation.add(participation);
    }

    public void deleteParticipationByOneSide(Participation participation){
        if(this.participation.contains(participation))
            this.participation.remove(participation);
    }

    private void deleteParticipationWithTheSameTournamentName(String tournamentName){
        Participation participation = this.participation.stream()
                .filter(participation1 -> participation1.getParticipatedTournament().getName().equals(tournamentName))
                .findFirst().orElse(null);
        if(participation!=null){
            this.participation.remove(participation);
        }
    }

    public void addBattlesByOneSide(Play battle){
        this.battles.add(battle);
    }

    public void setParticipation(List<Participation> participation){
        this.participation = participation;
    }
}
