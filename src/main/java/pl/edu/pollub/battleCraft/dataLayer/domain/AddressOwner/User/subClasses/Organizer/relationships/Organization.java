package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Organization{

    public Organization(Organizer organizer, Tournament organizedTournament) {
        this.organizer = organizer;
        this.organizedTournament = organizedTournament;
        this.accepted = false;
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

    @JsonIgnore
    public Organization copy(){
        return new Organization(this.organizer,this.organizedTournament,this.accepted);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;

        Organization that = (Organization) o;

        if (isAccepted() != that.isAccepted()) return false;
        if (getOrganizer() != null ? getOrganizer().getName() != null ? !getOrganizer().getName().equals(that.getOrganizer().getName()) : getOrganizer().getName() != null : that.getOrganizer() != null)
            return false;
        return getOrganizedTournament() != null ? getOrganizedTournament().getName().equals(that.getOrganizedTournament().getName()) : that.getOrganizedTournament() == null;
    }

    @Override
    public int hashCode() {
        int result =  0;
        result = 31 * result + (getOrganizer() != null ? getOrganizer().getName().hashCode() : 0);
        result = 31 * result + (getOrganizedTournament() != null ? getOrganizedTournament().getName().hashCode() : 0);
        result = 31 * result + (isAccepted() ? 1 : 0);
        return result;
    }
}
