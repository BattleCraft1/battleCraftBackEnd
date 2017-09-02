package pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import pl.edu.pollub.battleCraft.data.entities.Tournament.Tournament;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.relationships.Organization;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@DiscriminatorValue(value= UserType.Values.ORGANIZER)
@SecondaryTable(name = "organizers", pkJoinColumns = {@PrimaryKeyJoinColumn(name="id", referencedColumnName = "id")})

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
public class Organizer extends Player{
    public Organizer(){
        this.setBanned(false);
    }

    @OneToMany(orphanRemoval = true,cascade = CascadeType.ALL , mappedBy="organizer")
    private List<Organization> organizedTournaments;

    public void addOrganizedTournaments(Tournament... tournaments){
        this.organizedTournaments.addAll(Arrays.stream(tournaments)
                .map(tournament -> new Organization(this,tournament))
                .collect(Collectors.toList()));
    }
}
