package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInvitationResponseDTO extends InvitationResponseDTO{
    public PlayerInvitationResponseDTO(String secondPlayerName,boolean secondPlayerAccept, String tournamentName,boolean accepted){
        super(tournamentName,accepted);
        this.secondPlayerName = secondPlayerName;
        this.secondPlayerAccept = secondPlayerAccept;
    }
    private String secondPlayerName;
    private boolean secondPlayerAccept;
}
