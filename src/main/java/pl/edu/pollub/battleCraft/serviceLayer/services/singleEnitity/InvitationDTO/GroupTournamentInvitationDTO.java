package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.InvitationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.NullPlayer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;

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
