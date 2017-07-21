package pl.edu.pollub.battleCraft.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.entities.enums.TournamentClass;

import javax.persistence.*;
import java.util.Date;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Tournament extends AddressOwner{

    public Tournament(String name, TournamentClass tournamentClass, short maxPlayers, short tablesCount, Date dateOfStart, boolean active, boolean banned) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.tablesCount = tablesCount;
        this.dateOfStart = dateOfStart;
        this.active = active;
        this.tournamentClass = tournamentClass;
        this.banned = banned;
    }

    public Tournament(String name, TournamentClass tournamentClass ,short maxPlayers, short tablesCount, Date dateOfStart, boolean active, boolean banned, Game game, Address address) {
        super(address);
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.tablesCount = tablesCount;
        this.dateOfStart = dateOfStart;
        this.active = active;
        this.banned = banned;
        this.game = game;
        this.tournamentClass = tournamentClass;
    }

    @Column(length = 100, unique = true)
    private String name;

    private short maxPlayers;

    private short tablesCount;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm:ss dd-MM-yyyy")
    private Date dateOfStart;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn
    private Game game;

    @Enumerated(EnumType.STRING)
    private TournamentClass tournamentClass;

    private boolean banned;
}
