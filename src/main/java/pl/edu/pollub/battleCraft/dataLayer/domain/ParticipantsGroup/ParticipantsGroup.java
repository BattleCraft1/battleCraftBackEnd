package pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParticipantsGroup {

    public ParticipantsGroup(List<Participation> participation){
        this.participation = participation;
        participation.forEach(participation1 -> participation1.setParticipantsGroup(this));
    }

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true, mappedBy = "participantsGroup")
    private List<Participation> participation = new ArrayList<>();

    static public boolean checkIfParticipantsAreInTheSameGroup(Participation... manyParticipation){
        Long groupNumber = manyParticipation[0].getParticipantsGroup().getId();
        for(Participation participation:manyParticipation){
            if(!participation.getParticipantsGroup().getId().equals(groupNumber)){
                return false;
            }
        }
        return true;
    }
}
