package pl.edu.pollub.battleCraft.serviceLayer.services.invitation;


import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
class PlayerWithGroupDTO {
    private Player player;
    private ParticipantsGroup participantsGroup;
}
