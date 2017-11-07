package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation.InvitationDTO;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Organizer extends Player {
    public Organizer() {
        super(UserType.ORGANIZER);
    }

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true, mappedBy = "organizer")
    private List<Organization> organizations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private List<Game> createdGames = new ArrayList<>();

    public void editOrganizations(List<InvitationDTO> invitationDTOS, Tournament... organizedTournaments) {
        List<Tournament> tournamentList = Arrays.asList(organizedTournaments);
        this.organizations.stream().filter(organization -> tournamentList.contains(organization.getOrganizedTournament()))
                .forEach(organization -> {
                    boolean accepted = invitationDTOS.stream()
                            .filter(invitation -> invitation.getName().equals(organization.getOrganizedTournament().getName()))
                            .map(InvitationDTO::isAccepted)
                            .findFirst().orElse(false);
                    organization.setAccepted(accepted); });

        this.organizations.addAll(tournamentList.stream()
                .filter(tournament -> !this.organizations.stream()
                        .map(Organization::getOrganizedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .map(tournament -> {
                    boolean accepted = invitationDTOS.stream()
                            .filter(invitation -> invitation.getName().equals(tournament.getName()))
                            .map(InvitationDTO::isAccepted)
                            .findFirst().orElse(false);
                    Organization organization = new Organization(this, tournament, accepted);
                    tournament.addOrganizationByOneSide(organization);
                    return organization; })
                .collect(Collectors.toList()));

        this.organizations.removeAll(this.organizations.stream()
                .filter(organization -> !tournamentList.contains(organization.getOrganizedTournament())
                        && organization.getOrganizedTournament().getStatus()== TournamentStatus.ACCEPTED)
                .peek(organization -> {
                    organization.getOrganizedTournament().deleteOrganizationByOneSide(organization);
                    organization.setOrganizer(null);
                    organization.setOrganizedTournament(null);
                }).collect(Collectors.toList()));
    }

    public void addOrganizationByOneSide(Organization organization) {
        this.deleteOrganizationWithTheSameTournamentName(organization.getOrganizedTournament().getName());
        this.organizations.add(organization);
    }

    public void deleteOrganizationByOneSide(Organization organization){
        if(this.organizations.contains(organization))
        this.organizations.remove(organization);
    }

    private void deleteOrganizationWithTheSameTournamentName(String tournamentName){
        Organization organization = this.organizations.stream()
                .filter(participation1 -> participation1.getOrganizedTournament().getName().equals(tournamentName))
                .findFirst().orElse(null);
        if(organization!=null){
            this.organizations.remove(organization);
        }
    }

    public void editParticipation(List<Participation> participatedTournaments) {
        participatedTournaments.forEach(participation -> participation.setPlayer(this));
        this.setParticipatedTournaments(participatedTournaments);
    }

    private void setOrganizations(List<Organization> organizations){
        this.organizations = organizations;
    }

    private void setCreatedGames(List<Game> createdGames){
        this.createdGames = createdGames;
    }
}
