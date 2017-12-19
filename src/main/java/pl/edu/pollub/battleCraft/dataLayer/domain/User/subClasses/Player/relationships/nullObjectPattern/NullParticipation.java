package pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.nullObjectPattern;

import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.nullObjectPattern.NullTournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.nullObjectPattern.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.relationships.Participation;

public class NullParticipation extends Participation {
    public NullParticipation(){
        super();
        this.player = new NullPlayer();
        this.participatedTournament = new NullTournament(0);
        this.accepted = false;
        this.groupNumber = 0L;
    }
}
