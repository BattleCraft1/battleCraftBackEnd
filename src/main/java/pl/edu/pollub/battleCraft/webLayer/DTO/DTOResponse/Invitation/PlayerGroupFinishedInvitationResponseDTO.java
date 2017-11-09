package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGroupFinishedInvitationResponseDTO extends PlayerFinishedInvitationResponse{
    private String secondPlayerName;

    public PlayerGroupFinishedInvitationResponseDTO(String tournamentName, String secondPlayerName){
        super(tournamentName);
        this.secondPlayerName = secondPlayerName;
    }
}
