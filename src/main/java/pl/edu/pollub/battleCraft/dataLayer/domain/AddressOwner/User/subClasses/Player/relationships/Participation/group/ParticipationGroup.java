package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.Participation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ParticipationGroup {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "participationGroup")
    private Set<Participation> participationInGroup = new HashSet<>();

    public void addParticipation(Participation participation){
        participationInGroup.add(participation);
        participation.setParticipationGroup(this);
    }

    public void addManyParticipation(List<Participation> manyParticipation){
        participationInGroup.addAll(manyParticipation);
        manyParticipation.forEach(participation -> participation.setParticipationGroup(this));
    }

    public void removeParticipation(Participation participation){
        participationInGroup.remove(participation);
        participation.setParticipationGroup(null);
    }

    public void removeManyParticipation(List<Participation> manyParticipation){
        participationInGroup.removeAll(manyParticipation);
        manyParticipation.forEach(participation -> participation.setParticipationGroup(null));
    }
}
