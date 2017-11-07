package pl.edu.pollub.battleCraft.dataLayer.domain.Tour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import javax.persistence.*;
import java.util.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "randomGenerator")
@ToString
public class Tour {

    public Tour(int number,Tournament tournament){
        this.number = number;
        this.tournament = tournament;
        int battlesCount = createNumberOfBattles(tournament.getParticipation().size());
        for(int battleNumber=0;battleNumber<battlesCount;battleNumber++){
            battles.add(new Battle(this,battleNumber));
        }
    }

    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

    private int number;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "tour")
    private List<Battle> battles = new ArrayList<>();

    @Transient
    private Random randomGenerator = new Random();

    @JsonIgnore
    private int createNumberOfBattles(int number){
        if(number%2==0){
            return number/2;
        }
        else{
            return this.createNumberOfBattlesWithOneBattlesWithAlonePlayer(number);
        }
    }

    @JsonIgnore
    private int createNumberOfBattlesWithOneBattlesWithAlonePlayer(int number){
        return number/2+1;
    }
}
