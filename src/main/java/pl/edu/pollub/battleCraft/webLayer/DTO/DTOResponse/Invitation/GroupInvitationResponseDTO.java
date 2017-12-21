package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Invitation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GroupInvitationResponseDTO extends InvitationResponseDTO{
    public GroupInvitationResponseDTO(String tournamentName, String secondPlayerName, boolean secondPlayerAccept, boolean accepted){
        super(tournamentName,accepted);
        this.secondPlayerName = secondPlayerName;
        this.secondPlayerAccept = secondPlayerAccept;
    }
    private String secondPlayerName;
    private boolean secondPlayerAccept;
}
