package pl.edu.pollub.battleCraft.data.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Formula;
import pl.edu.pollub.battleCraft.data.entities.enums.TournamentClass;
import pl.edu.pollub.battleCraft.data.entities.enums.TournamentStatus;

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

    public Tournament(String name, TournamentClass tournamentClass, int maxPlayers, int tablesCount,
                      Date dateOfStart, TournamentStatus tournamentStatus, boolean banned) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.tablesCount = tablesCount;
        this.dateOfStart = dateOfStart;
        this.tournamentClass = tournamentClass;
        this.tournamentStatus = tournamentStatus;
        this.banned = banned;
    }

    public Tournament(String name, TournamentClass tournamentClass ,int maxPlayers, int tablesCount, Date dateOfStart
            , Game game, Address address, List<Participation> participants, TournamentStatus tournamentStatus, boolean banned) {
        super(address);
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.tablesCount = tablesCount;
        this.dateOfStart = dateOfStart;
        this.game = game;
        this.tournamentClass = tournamentClass;
        this.participants = participants;
        this.banned = banned;
        this.tournamentStatus = tournamentStatus;
    }

    @Column(length = 30, unique = true)
    private String name;

    private int maxPlayers;

    private int tablesCount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfStart;

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

    @Formula("(select count(*) from participation p where p.tournament_id = id)")
    private int playersNumber;

    @Formula("max_players-(select count(*) from participation p where p.tournament_id = id)")
    private int freeSlots;
}
