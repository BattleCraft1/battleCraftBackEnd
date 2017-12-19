package pl.edu.pollub.battleCraft.serviceLayer.services.invitation.InvitationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.nullObjectPattern.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupTournamentInvitationDTO extends InvitationDTO{
    public GroupTournamentInvitationDTO(Tournament tournament, boolean accepted, Player player) {
        super(tournament, accepted);
        if(player!=null)
            this.secondPlayer = player;
        else
            this.secondPlayer = new NullPlayer();
    }
    private Player secondPlayer;
}
