package pl.edu.pollub.battleCraft.data.entities.User.subClasses.players;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Participation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Inheritance(strategy = InheritanceType.JOINED)

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Player extends UserAccount {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "player")
    private List<Participation> participatedTournaments = new ArrayList<>();
    private boolean banned;

    public Player() {
        super(UserType.PLAYER);
        this.banned = false;
    }

    protected Player(UserType userType) {
        super(userType);
        this.banned = false;
    }

    public void setParticipatedTournaments(Tournament... tournaments) {
        this.participatedTournaments = Arrays.stream(tournaments)
                .map(tournament -> {
                    Participation participation = new Participation(this, tournament);
                    tournament.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList());
    }

    public void setParticipatedTournaments(List<Tournament> tournaments) {
        this.participatedTournaments = tournaments.stream()
                .map(tournament -> {
                    Participation participation = new Participation(this, tournament);
                    tournament.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList());
    }

    public void addParticipatedTournaments(Tournament... tournaments) {
        this.participatedTournaments.addAll(Arrays.stream(tournaments)
                .map(tournament -> {
                    Participation participation = new Participation(this, tournament);
                    tournament.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList()));
    }

    public void addParticipatedTournaments(List<Tournament> tournaments) {
        this.participatedTournaments.addAll(tournaments.stream()
                .map(tournament -> {
                    Participation participation = new Participation(this, tournament);
                    tournament.addParticipationByOneSide(participation);
                    return participation;
                })
                .collect(Collectors.toList()));
    }

    public void addParticipation(Participation participation) {
        this.participatedTournaments.add(participation);
    }
}
