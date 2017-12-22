package pl.edu.pollub.battleCraft.dataLayer.domain.Organization;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
public class Organization{

    public Organization(Organizer organizer, Tournament organizedTournament) {
        this.organizer = organizer;
        this.organizedTournament = organizedTournament;
        this.accepted = true;//true only for test
    }

    public Organization(Organizer organizer, Tournament organizedTournament, boolean accepted) {
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
}
