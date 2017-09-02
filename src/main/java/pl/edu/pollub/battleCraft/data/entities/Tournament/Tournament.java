package pl.edu.pollub.battleCraft.data.entities.Tournament;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.data.entities.Address.Address;
import pl.edu.pollub.battleCraft.data.entities.Address.AddressOwner;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.Tournament.enums.TournamentStatus;

import javax.persistence.*;
import java.io.Serializable;
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

    public Tournament() {
        this.tournamentStatus = TournamentStatus.NEW;
        this.banned = false;
    }

    @Column(length = 30, unique = true)
    private String name;

    private int maxPlayers;

    private int tablesCount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfStart;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfEnd;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn
    private Game game;

    @Enumerated(EnumType.STRING)
    private TournamentClass tournamentClass;

    @Enumerated(EnumType.STRING)
    private TournamentStatus tournamentStatus;

    private boolean banned;

    @JsonIgnore
    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL , mappedBy = "tournament")
    private List<Participation> participants;

    @JsonIgnore
    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL , mappedBy = "tournament")
    private List<Organization> organizers;

    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;

    @Formula("max_players-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;

    public void setParticipants(Player... participants){
        this.participants = Arrays.asList(participants);
    }

    public void addParticipants(Player... participants){
        this.participants.addAll(Arrays.stream(participants)
                .map(participant -> {
                    return new Participation(participant,this);
                })
                .collect(Collectors.toList()));
    }

    public void addOrganizers(Organizer... organizers){
        this.organizers.addAll(Arrays.stream(organizers)
                .map(organizer -> new Organization(organizer,this))
                .collect(Collectors.toList()));
    }
}
