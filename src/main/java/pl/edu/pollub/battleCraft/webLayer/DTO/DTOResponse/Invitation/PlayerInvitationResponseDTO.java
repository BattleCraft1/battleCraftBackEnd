package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInvitationResponseDTO extends InvitationResponseDTO{
    public PlayerInvitationResponseDTO(String secondPlayerName,boolean accepted, String tournamentName){
        super(tournamentName,accepted);
        this.secondPlayerName = secondPlayerName;
    }
    private String secondPlayerName;
}
