package pl.edu.pollub.battleCraft.dataLayer.entities.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.dataLayer.entities.Address.AddressOwner;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Participation;

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
    private List<Participation> participants = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "organizedTournament")
    private List<Organization> organizers = new ArrayList<>();

    private int tablesCount;

    private int playersOnTableCount;

    @Formula("tables_count * players_on_table_count")
    private int maxPlayers;

    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;

    @Formula("(tables_count * players_on_table_count)-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;

    public void addOrganizers(Organizer... organizers){
        this.organizers.addAll(Arrays.stream(organizers)
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganization(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void addOrganization(Organization organization) {
        this.deleteOrganizationWithTheSameTournamentName(organization.getOrganizedTournament().getName());
        this.organizers.add(organization);
    }

    public void editOrganizers(Organizer... organizers) {
        List<Organizer> organizersList = Arrays.asList(organizers);
        this.organizers.addAll(organizersList.stream()
                .filter(organizer -> !this.organizers.stream()
                                        .map(Organization::getOrganizer)
                                        .collect(Collectors.toList()).contains(organizer))
                        .map(organizer -> {
                            Organization organization = new Organization(organizer, this);
                            organizer.addOrganization(organization);
                            return organization; })
                        .collect(Collectors.toList()));
        this.organizers.removeAll(this.organizers.stream()
                .filter(organization -> !organizersList.contains(organization.getOrganizer()))
                .peek(organization -> {
                    organization.getOrganizer().deleteOrganization(organization);
                    organization.setOrganizedTournament(null);
                    organization.setOrganizer(null);
                }).collect(Collectors.toList()));
    }

    public void chooseGame(Game game){
        this.game = game;
        game.addTournamentByOneSide(this);
    }

    public void addParticipants(Player... participants){
        this.participants = Arrays.stream(participants)
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipation(participation);
                    return participation;
                })
                .collect(Collectors.toList());
    }

    public void addParticipation(Participation participation) {
        this.deleteParticipationWithTheSameTournamentName(participation.getParticipatedTournament().getName());
        this.participants.add(participation);
    }

    private void deleteParticipationWithTheSameTournamentName(String playerName){
        Participation participation = this.participants.stream()
                .filter(participation1 -> participation1.getPlayer().getName().equals(playerName))
                .findFirst().orElse(null);
        if(participation!=null){
            this.participants.remove(participation);
        }
    }

    private void deleteOrganizationWithTheSameTournamentName(String organizerName){
        Organization organization = this.organizers.stream()
                .filter(organization1 -> organization1.getOrganizer().getName().equals(organizerName))
                .findFirst().orElse(null);
        if(organization!=null){
            this.participants.remove(organization);
        }
    }


    public void editParticipants(Player... participants) {
        List<Player> participantsList = Arrays.asList(participants);
        this.participants.addAll(participantsList.stream()
                .filter(participant -> !this.participants.stream()
                        .map(Participation::getPlayer)
                        .collect(Collectors.toList()).contains(participant))
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipation(participation);
                    return participation; })
                .collect(Collectors.toList()));
        this.participants.removeAll(this.participants.stream()
                .filter(participation -> !participantsList.contains(participation.getPlayer()))
                .peek(participation -> {
                    participation.getPlayer().deleteParticipation(participation);
                    participation.setPlayer(null);
                    participation.setParticipatedTournament(null);
                }).collect(Collectors.toList()));
    }

    protected void setMaxPlayers(int maxPlayers){
        this.maxPlayers = maxPlayers;
    }

    protected void setGame(Game game){
        this.game = game;
    }

    protected void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }

    protected void setFreeSlots(int freeSlots) {
        this.freeSlots = freeSlots;
    }

    private void setParticipants(List<Participation> participants){
        this.participants = participants;
    }

    private void setOrganizers(List<Organization> organizers){
        this.organizers = organizers;
    }
}
