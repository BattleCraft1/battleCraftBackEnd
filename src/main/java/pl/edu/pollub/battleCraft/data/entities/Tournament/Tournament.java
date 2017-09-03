package pl.edu.pollub.battleCraft.data.entities.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.data.entities.Address.AddressOwner;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Tournament extends AddressOwner implements Serializable {

    @Column(length = 30, unique = true)
    private String name;
    private int maxPlayers;
    private int tablesCount;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfStart;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfEnd;
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn
    private Game game;
    @Enumerated(EnumType.STRING)
    private TournamentClass tournamentClass;
    @Enumerated(EnumType.STRING)
    private TournamentStatus tournamentStatus;
    private boolean banned;
    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Participation> participants = new ArrayList<>();
    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tournament")
    private List<Organization> organizers = new ArrayList<>();
    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;
    @Formula("max_players-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;

    public Tournament() {
        this.tournamentStatus = TournamentStatus.NEW;
        this.banned = false;
    }

    public void setGame(Game game){
        this.game = game;
        game.addTournament(this);
    }

    public void setGameByOneSide(Game game){
        this.game = game;
    }

    public void setParticipants(Player... participants) {
        this.participants = Arrays.stream(participants)
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipation(participation);
                    return participation;
                })
                .collect(Collectors.toList());
    }

    public void setParticipants(List<Player> participants) {
        this.participants = participants.stream()
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipation(participation);
                    return participation;
                })
                .collect(Collectors.toList());
    }

    public void setOrganizers(Organizer... organizers) {
        this.organizers = Arrays.stream(organizers)
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganization(organization);
                    return organization;
                })
                .collect(Collectors.toList());
    }

    public void setOrganizers(List<Organizer> organizers) {
        this.organizers = organizers.stream()
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganization(organization);
                    return organization;
                })
                .collect(Collectors.toList());
    }

    public void addParticipants(Player... participants) {
        this.participants.addAll(Arrays.stream(participants)
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipation(participation);
                    return participation;
                })
                .collect(Collectors.toList()));
    }

    public void addParticipants(List<Player> participants) {
        this.participants.addAll(participants.stream()
                .map(participant -> {
                    Participation participation = new Participation(participant, this);
                    participant.addParticipation(participation);
                    return participation;
                })
                .collect(Collectors.toList()));
    }

    public void addOrganizers(Organizer... organizers) {
        this.organizers.addAll(Arrays.stream(organizers)
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganization(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void addOrganizers(List<Organizer> organizers) {
        this.organizers.addAll(organizers.stream()
                .map(organizer -> {
                    Organization organization = new Organization(organizer, this);
                    organizer.addOrganization(organization);
                    return organization;
                })
                .collect(Collectors.toList()));
    }

    public void addParticipationByOneSide(Participation participation) {
        this.participants.add(participation);
    }

    public void addOrganizationByOneSide(Organization organization) {
        this.organizers.add(organization);
    }
}
