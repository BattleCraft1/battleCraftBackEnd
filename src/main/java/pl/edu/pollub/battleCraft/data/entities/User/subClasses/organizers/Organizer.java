package pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "organizer")
    private List<Organization> organizedTournaments = new ArrayList<>();

    public void setOrganizedTournaments(Tournament... tournaments) {
        this.organizedTournaments = Arrays.stream(tournaments)
                .map(tournament -> {
                    Organization organization = new Organization(this, tournament);
                    tournament.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList());
    }

    public void setOrganizedTournaments(List<Tournament> tournaments) {
        this.organizedTournaments = tournaments.stream()
                .map(tournament -> {
                    Organization organization = new Organization(this, tournament);
                    tournament.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList());
    }

    public void addOrganizedTournaments(Tournament... tournaments) {
        this.organizedTournaments.addAll(Arrays.stream(tournaments)
                .map(tournament -> {
                    Organization organization = new Organization(this, tournament);
                    tournament.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void addOrganizedTournaments(List<Tournament> tournaments) {
        this.organizedTournaments.addAll(tournaments.stream()
                .map(tournament -> {
                    Organization organization = new Organization(this, tournament);
                    tournament.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void addOrganization(Organization organization) {
        this.organizedTournaments.add(organization);
    }
}
