package pl.edu.pollub.battleCraft.serviceLayer.services.invitation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO.InvitationDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationToOrganizationService {

    public void sendInvitations(Organizer organizer, List<InvitationDTO> invitationDTOS) {
        this.modifyExistingOrganizations(organizer,invitationDTOS);
        List<Tournament> organizedTournaments = invitationDTOS.stream().map(InvitationDTO::getTournament).collect(Collectors.toList());
        this.addNewOrganizations(organizer,invitationDTOS,organizedTournaments);
        this.removeNotExistingOrganizations(organizer,organizedTournaments);
    }

    private void modifyExistingOrganizations(Organizer organizer, List<InvitationDTO> invitationDTOS){
        List<String> tournamentsNamesFromInvitations =
                invitationDTOS.stream().map(invitationDTO -> invitationDTO.getTournament().getName())
                        .collect(Collectors.toList());

        organizer.getOrganizations().stream().filter(organization ->
                tournamentsNamesFromInvitations.contains(organization.getOrganizedTournament().getName()))
                .forEach(organization -> {
                    boolean accepted = this.checkIfInvitationIsAccepted(organization.getOrganizedTournament().getName(), invitationDTOS);
                    organization.setAccepted(accepted); });
    }

    private void addNewOrganizations(Organizer organizer, List<InvitationDTO> invitationDTOS, List<Tournament> organizedTournaments){
        organizer.getOrganizations().addAll(organizedTournaments.stream()
                .filter(tournament -> !organizer.getOrganizations().stream()
                        .map(Organization::getOrganizedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .filter(tournament -> !tournament.isBanned())
                .map(tournament -> {
                    boolean accepted = this.checkIfInvitationIsAccepted(tournament.getName(), invitationDTOS);
                    Organization organization = new Organization(organizer, tournament, accepted);
                    tournament.addOrganizationByOneSide(organization);
                    return organization; })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingOrganizations(Organizer organizer, List<Tournament> organizedTournaments){
        organizer.getOrganizations().removeAll(organizer.getOrganizations().stream()
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

}
