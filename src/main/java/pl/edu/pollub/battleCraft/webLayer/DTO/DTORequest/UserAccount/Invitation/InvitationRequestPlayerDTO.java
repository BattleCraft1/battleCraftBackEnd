package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequestPlayerDTO {
    private String name;
    private boolean accepted;
    private String secondPlayerName;

    public String getTournamentName(){
        return this.name;
    }
}
