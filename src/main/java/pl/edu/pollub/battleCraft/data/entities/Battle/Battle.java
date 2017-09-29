package pl.edu.pollub.battleCraft.data.entities.Battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Tour.Tour;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.relationships.Play;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Battle {

    public Battle(int tableNumber){
        this.tableNumber = tableNumber;
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int tableNumber;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "battle")
    private List<Play> plays = new ArrayList<>();
}
