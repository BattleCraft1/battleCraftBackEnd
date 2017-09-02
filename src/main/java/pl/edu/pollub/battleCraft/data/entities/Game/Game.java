package pl.edu.pollub.battleCraft.data.entities.Game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Game implements Serializable {

    public Game(String name) {
        this.name=name;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    @Column(length = 30)
    private String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "game", cascade = CascadeType.ALL)
    private List<Tournament> tournaments;

    public void setTournaments(List<Tournament> tournaments){
        this.tournaments = tournaments;
        tournaments.forEach(tournament -> tournament.setGame(this));
    }

    public void setTournaments(Tournament... tournaments){
        this.tournaments = Arrays.asList(tournaments);
        Arrays.stream(tournaments).forEach(tournament -> tournament.setGame(this));
    }

    public void addTournaments(List<Tournament> tournaments){
        this.tournaments.addAll(tournaments);
        tournaments.forEach(tournament -> tournament.setGame(this));
    }

    public void addTournaments(Tournament... tournaments){
        this.tournaments.addAll(Arrays.asList(tournaments));
        Arrays.stream(tournaments).forEach(tournament -> tournament.setGame(this));
    }
}
