package pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.relationships.Participation.group.ParticipationGroup;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Participation{

    public Participation(Player player, Tournament participatedTournament) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = true;
    }

    private Participation(Player player, Tournament participatedTournament, boolean accepted) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = accepted;
    }

    public Participation(Player player, Tournament participatedTournament, ParticipationGroup group) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = true;
        this.chooseGroup(group);
    }

    private Participation(Player player, Tournament participatedTournament, boolean accepted, ParticipationGroup group) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = accepted;
        this.chooseGroup(group);
    }
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "player_id")
    protected Player player;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "group_id")
    protected ParticipationGroup participationGroup;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tournament_id")
    protected Tournament participatedTournament;

    protected boolean accepted;

    public void chooseGroup(ParticipationGroup group){
        this.setParticipationGroup(group);
        group.getParticipationInGroup().add(this);
    }

    public void removeGroup(){
        this.getParticipationGroup().getParticipationInGroup().remove(this);
        this.setParticipationGroup(null);
    }

    @JsonIgnore
    public Participation copy(){
        return new Participation(this.player,this.participatedTournament,this.accepted,this.participationGroup);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Participation)) return false;

        Participation that = (Participation) o;

        if (isAccepted() != that.isAccepted()) return false;
        if (getPlayer() != null ? getPlayer().getName() != null ? !getPlayer().getName().equals(that.getPlayer().getName()) : getPlayer().getName() != null : that.getPlayer() != null) return false;
        if (getParticipationGroup() != null ? getParticipationGroup() != null ? !getParticipationGroup().getId().equals(that.getParticipationGroup().getId()) : that.getParticipationGroup().getId() != null : that.getParticipationGroup() != null) return false;
        return getParticipatedTournament() != null ? getParticipatedTournament().getName() != null ? getParticipatedTournament().getName().equals(that.getParticipatedTournament().getName()) : that.getParticipatedTournament().getName() == null : that.getParticipatedTournament() == null;
    }

    @Override
    public int hashCode() {
        int result = getPlayer() != null ? getPlayer().getName() != null ? getPlayer().getName().hashCode() : 0 : 0;
        result = 31 * result + (getParticipationGroup() != null ? getParticipationGroup().getId() != null ? getParticipationGroup().getId().hashCode() : 0 : 0);
        result = 31 * result + (getParticipatedTournament() != null ? getParticipatedTournament().getName() != null ? getParticipatedTournament().getName().hashCode() : 0 : 0);
        result = 31 * result + (isAccepted() ? 1 : 0);
        return result;
    }
}
