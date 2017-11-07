package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.AddressOwner;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@Inheritance(strategy = InheritanceType.JOINED)
public class Tournament extends AddressOwner{
    public Tournament() {
        super();
        this.status = TournamentStatus.NEW;
        this.banned = false;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Column(length = 30, unique = true)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfStart;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfEnd;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn
    private Game game;

    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    private boolean banned;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "participatedTournament")
    private List<Participation> participation = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "organizedTournament")
    private List<Organization> organizations = new ArrayList<>();

    private int tablesCount;

    private int playersOnTableCount;

    private int toursCount;

    @Formula("tables_count * players_on_table_count")
    private int maxPlayers;

    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;

    @Formula("(tables_count * players_on_table_count)-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Tour> tours = new ArrayList<>();

    private int currentTourNumber;

    public void filterNoAcceptedParticipation(){
        this.participation = this.participation.stream().filter(Participation::isAccepted).collect(Collectors.toList());
    }

    public void filterNoAcceptedOrganizations(){
        this.organizations = this.organizations.stream().filter(Organization::isAccepted).collect(Collectors.toList());
    }

    public void addOrganizers(Organizer... organizers){
        this.organizations.addAll(Arrays.stream(organizers)
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganizationByOneSide(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void editOrganizers(Organizer... organizers) {
        List<Organizer> organizersList = Arrays.asList(organizers);
        this.organizations.addAll(organizersList.stream()
                .filter(organizer -> !this.organizations.stream()
                        .map(Organization::getOrganizer)
                        .collect(Collectors.toList()).contains(organizer))
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganizationByOneSide(organization);
                    return organization; })
                .collect(Collectors.toList()));
        this.organizations.removeAll(this.organizations.stream()
                .filter(organization -> !organizersList.contains(organization.getOrganizer()))
                .peek(organization -> {
                    organization.getOrganizer().deleteOrganizationByOneSide(organization);
                    organization.setOrganizedTournament(null);
                    organization.setOrganizer(null);
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

    private void deleteOrganizationWithTheSameTournamentName(String organizerName){
        Organization organization = this.organizations.stream()
                .filter(organization1 -> organization1.getOrganizer().getName().equals(organizerName))
                .findFirst().orElse(null);
        if(organization!=null){
            this.organizations.remove(organization);
        }
    }

    public void addParticipants(Player... participants){
        this.participation = Arrays.stream(participants)
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList());
    }

    public void editParticipants(Player... participants) {
        List<Player> participantsList = Arrays.asList(participants);
        this.participation.addAll(participantsList.stream()
                .filter(participant -> !this.participation.stream()
                        .map(Participation::getPlayer)
                        .collect(Collectors.toList()).contains(participant))
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipationByOneSide(participation);
                    return participation; })
                .collect(Collectors.toList()));
        this.participation.removeAll(this.participation.stream()
                .filter(participation -> !participantsList.contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipationByOneSide(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    public void addParticipationByOneSide(Participation participation) {
        this.deleteParticipationWithTheSameTournamentName(participation.getParticipatedTournament().getName());
        this.participation.add(participation);
    }

    public void deleteParticipationByOneSide(Participation participation){
        if(this.participation.contains(participation))
            this.participation.remove(participation);
    }

    private void deleteParticipationWithTheSameTournamentName(String playerName){
        Participation participation = this.participation.stream()
                .filter(participation1 -> participation1.getPlayer().getName().equals(playerName))
                .findFirst().orElse(null);
        if(participation!=null){
            this.participation.remove(participation);
        }
    }

    public void chooseGame(Game game){
        this.game = game;
        game.addTournamentByOneSide(this);
    }

    protected void setGame(Game game){
        this.game = game;
    }

    private void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    private void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    private void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    private void setParticipation(List<Participation> participation){
        this.participation = participation;
    }

    private void setOrganizations(List<Organization> organizations){
        this.organizations = organizations;
    }
}
