package pl.edu.pollub.battleCraft.dataLayer.domain.Battle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tour.Tour;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Play;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Transient
    private final int NUMBER_OF_POINTS_FOR_ALONE_PLAYER = 17;

    public Battle(Tour tour, int tableNumber){
        this.tableNumber = tableNumber;
        this.setTour(tour);
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
    private List<Play> players = new ArrayList<>();
}
