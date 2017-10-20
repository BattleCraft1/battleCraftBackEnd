package pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.relationships.Participation;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
public class Organization implements Cloneable{

    public Organization(Organizer organizer, Tournament organizedTournament) {
        this.organizer = organizer;
        this.organizedTournament = organizedTournament;
        this.accepted = false;
    }

    private Organization(Organizer organizer, Tournament organizedTournament,boolean accepted) {
        this.organizer = organizer;
        this.organizedTournament = organizedTournament;
        this.accepted = accepted;
    }

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "organizer_id")
    private Organizer organizer;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tournament_id")
    private Tournament organizedTournament;

    private boolean accepted;

    @JsonIgnore
    public Organization clone(){
        return new Organization(this.organizer,this.organizedTournament,this.accepted);
    }
}
