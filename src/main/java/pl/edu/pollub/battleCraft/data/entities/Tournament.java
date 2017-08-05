package pl.edu.pollub.battleCraft.data.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.data.entities.enums.TournamentClass;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Tournament extends AddressOwner implements Serializable {

    public Tournament(String name, TournamentClass tournamentClass, int maxPlayers, int tablesCount, Date dateOfStart,
                      boolean active, boolean banned, boolean accepted) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.tablesCount = tablesCount;
        this.dateOfStart = dateOfStart;
        this.active = active;
        this.tournamentClass = tournamentClass;
        this.banned = banned;
        this.accepted = accepted;
    }

    public Tournament(String name, TournamentClass tournamentClass ,int maxPlayers, int tablesCount, Date dateOfStart, boolean active,
                      boolean banned, Game game, Address address, List<Participation> participants, boolean accepted) {
        super(address);
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.tablesCount = tablesCount;
        this.dateOfStart = dateOfStart;
        this.active = active;
        this.banned = banned;
        this.game = game;
        this.tournamentClass = tournamentClass;
        this.participants = participants;
        this.accepted = accepted;
    }

    @Column(length = 100, unique = true)
    private String name;

    private int maxPlayers;

    private int tablesCount;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="hh:mm:ss dd-MM-yyyy")
    private Date dateOfStart;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn
    private Game game;

    @Enumerated(EnumType.STRING)
    private TournamentClass tournamentClass;

    private boolean banned;

    private boolean accepted;

    @JsonIgnore
    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "tournament")
    private List<Participation> participants;

    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;

    @Formula("max_players-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;
}
