package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.InvitationDTO;

import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
public class DuelTournamentInvitationDTO extends InvitationDTO{
    public DuelTournamentInvitationDTO(Tournament tournament, boolean accepted) {
        super(tournament, accepted);
    }
}
