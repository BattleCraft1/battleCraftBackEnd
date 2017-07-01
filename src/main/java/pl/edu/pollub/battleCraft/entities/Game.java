package pl.edu.pollub.battleCraft.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class Game {

    public Game() {
    }

    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "game")
    private List<Tournament> tournaments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
