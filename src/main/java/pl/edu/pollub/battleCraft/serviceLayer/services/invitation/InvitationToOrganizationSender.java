package pl.edu.pollub.battleCraft.serviceLayer.services.invitation;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.Organization.Organization;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvitationToOrganizationSender {

    public void inviteOrganizersList(Tournament tournament, List<Organizer> organizers){
        tournament.getOrganizations().addAll(organizers.stream()
                .map(organizer -> {
                    Organization organization = new Organization(organizer, tournament);
                    organizer.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void inviteEditedOrganizersList(Tournament tournament, List<Organizer> organizers) {
        this.addNewOrganizations(tournament, organizers);
        this.removeNotExistingOrganizations(tournament, organizers);

    }

    private void addNewOrganizations(Tournament tournament, List<Organizer> organizers){
        List<Organization> organizations = tournament.getOrganizations();

        organizations.addAll(organizers.stream()
                .filter(organizer -> !organizations.stream()
                        .map(Organization::getOrganizer)
                        .collect(Collectors.toList()).contains(organizer))
                .map(organizer -> {
                    Organization organization = new Organization(organizer, tournament);
                    organizer.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    private void removeNotExistingOrganizations(Tournament tournament, List<Organizer> organizers){
        List<Organization> organizations = tournament.getOrganizations();

        organizations.removeAll(organizations.stream()
                .filter(organization -> !organizers.contains(organization.getOrganizer()))
                .peek(organization -> {
                    organization.getOrganizer().deleteOrganizationByOneSide(organization);
                    organization.setOrganizedTournament(null);
                    organization.setOrganizer(null);
                }).collect(Collectors.toList()));
    }

}
