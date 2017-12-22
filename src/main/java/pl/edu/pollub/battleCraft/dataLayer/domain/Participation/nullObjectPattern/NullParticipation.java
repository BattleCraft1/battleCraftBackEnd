package pl.edu.pollub.battleCraft.dataLayer.domain.Participation.nullObjectPattern;

import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.nullObjectPattern.NullTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.nullObjectPattern.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.Participation;

public class NullParticipation extends Participation {
    public NullParticipation(){
        super();
        this.id = -1L;
        this.player = new NullPlayer();
        this.participatedTournament = new NullTournament(0);
        this.accepted = false;
        this.participantsGroup = new ParticipantsGroup();
    }
}
