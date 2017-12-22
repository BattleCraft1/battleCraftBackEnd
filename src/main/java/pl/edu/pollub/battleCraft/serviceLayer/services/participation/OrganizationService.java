package pl.edu.pollub.battleCraft.serviceLayer.services.participation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.Organization.Organization;
import pl.edu.pollub.battleCraft.serviceLayer.services.participation.ParticipationDTO.ParticipationDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    public void confirmOrganization(Organizer organizer, List<ParticipationDTO> organizationDTOs) {
        this.modifyExistingOrganizations(organizer,organizationDTOs);
        List<Tournament> organizedTournaments = organizationDTOs.stream().map(ParticipationDTO::getTournament).collect(Collectors.toList());
        this.addNewOrganizations(organizer,organizationDTOs,organizedTournaments);
        this.removeNotExistingOrganizations(organizer,organizedTournaments);
    }

    private void modifyExistingOrganizations(Organizer organizer, List<ParticipationDTO> organizationDTOs){
        List<String> tournamentsNamesFromInvitations =
                organizationDTOs.stream().map(invitationDTO -> invitationDTO.getTournament().getName())
                        .collect(Collectors.toList());

        organizer.getOrganizations().stream().filter(organization ->
                tournamentsNamesFromInvitations.contains(organization.getOrganizedTournament().getName()))
                .forEach(organization -> {
                    boolean accepted = this.checkIfInvitationIsAccepted(organization.getOrganizedTournament().getName(), organizationDTOs);
                    organization.setAccepted(accepted); });
    }

    private void addNewOrganizations(Organizer organizer, List<ParticipationDTO> organizationDTOs, List<Tournament> organizedTournaments){
        organizer.getOrganizations().addAll(organizedTournaments.stream()
                .filter(tournament -> !organizer.getOrganizations().stream()
                        .map(Organization::getOrganizedTournament)
                        .collect(Collectors.toList()).contains(tournament))
                .filter(tournament -> !tournament.isBanned())
                .map(tournament -> {
                    boolean accepted = this.checkIfInvitationIsAccepted(tournament.getName(), organizationDTOs);
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

    private boolean checkIfInvitationIsAccepted(String tournamentName, List<ParticipationDTO> invitationDTOS){
        return invitationDTOS.stream()
                .filter(invitation -> invitation.getTournament().getName()
                        .equals(tournamentName))
                .map(ParticipationDTO::isAccepted)
                .findFirst().orElse(false);
    }

}
