package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

import javax.persistence.*;

@Entity

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
public class Participation{

    public Participation(Player player, Tournament participatedTournament, ParticipantsGroup participantsGroup) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = true;//true only for test
        participantsGroup.getParticipation().add(this);
        this.participantsGroup = participantsGroup;
    }

    private Participation(Player player, Tournament participatedTournament, boolean accepted, ParticipantsGroup participantsGroup) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = accepted;
        participantsGroup.getParticipation().add(this);
        this.participantsGroup = participantsGroup;
    }

    public Participation(Player player, Tournament participatedTournament) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = true;//true only for test
        this.participantsGroup = null;
    }

    private Participation(Player player, Tournament participatedTournament, boolean accepted) {
        this.player = player;
        this.participatedTournament = participatedTournament;
        this.accepted = accepted;
        this.participantsGroup = null;
    }
    
    @Id
    @GeneratedValue
    protected Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "player_id")
    protected Player player;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "tournament_id")
    protected Tournament participatedTournament;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "participants_group_id")
    protected ParticipantsGroup participantsGroup;

    protected boolean accepted;
}
