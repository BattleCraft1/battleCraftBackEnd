package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.UserAccount.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequestDTO {
    private String name;
    private boolean accepted;

    public String getTournamentName(){
        return this.name;
    }
}

