package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.enums.UserType;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.InvitationDTO.InvitationDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation.InvitationRequestDTO;

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

    public void editOrganizations(List<InvitationDTO> invitationDTOS) {
        this.modifyExistingOrganizations(invitationDTOS);
        List<Tournament> organizedTournaments = invitationDTOS.stream().map(InvitationDTO::getTournament).collect(Collectors.toList());
        this.addNewOrganizations(invitationDTOS,organizedTournaments);
        this.removeNotExistingOrganizations(organizedTournaments);
    }

    private void modifyExistingOrganizations(List<InvitationDTO> invitationDTOS){
        List<String> tournamentsNamesFromInvitations =
                invitationDTOS.stream().map(invitationDTO -> invitationDTO.getTournament().getName())
                        .collect(Collectors.toList());

        this.organizations.stream().filter(organization ->
                tournamentsNamesFromInvitations.contains(organization.getOrganizedTournament().getName()))
                .forEach(organization -> {
                    boolean accepted = this.checkIfInvitationIsAccepted(organization.getOrganizedTournament().getName(), invitationDTOS);
                    organization.setAccepted(accepted); });
    }

    private void addNewOrganizations(List<InvitationDTO> invitationDTOS, List<Tournament> organizedTournaments){
        this.organizations.addAll(organizedTournaments.stream()
                .filter(tournament -> !this.organizations.stream()
                        .map(Organization::getOrganizedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .map(tournament -> {
                    boolean accepted = this.checkIfInvitationIsAccepted(tournament.getName(), invitationDTOS);
                    Organization organization = new Organization(this, tournament, accepted);
                    tournament.addOrganizationByOneSide(organization);
                    return organization; })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingOrganizations(List<Tournament> organizedTournaments){
        this.organizations.removeAll(this.organizations.stream()
                .filter(organization -> !organizedTournaments.contains(organization.getOrganizedTournament())
                        && organization.getOrganizedTournament().getStatus()== TournamentStatus.ACCEPTED)
                .peek(organization -> {
                    organization.getOrganizedTournament().deleteOrganizationByOneSide(organization);
                    organization.setOrganizer(null);
                    organization.setOrganizedTournament(null);
                }).collect(Collectors.toList()));
    }

    private boolean checkIfInvitationIsAccepted(String tournamentName, List<InvitationDTO> invitationDTOS){
        return invitationDTOS.stream()
                .filter(invitation -> invitation.getTournament().getName()
                        .equals(tournamentName))
                .map(InvitationDTO::isAccepted)
                .findFirst().orElse(false);
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

    private void setOrganizations(List<Organization> organizations){
        this.organizations = organizations;
    }

    private void setCreatedGames(List<Game> createdGames){
        this.createdGames = createdGames;
    }
}
