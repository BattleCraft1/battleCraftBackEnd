package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Participation;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FinishedParticipatedGroupTournamentResponseDTO extends FinishedParticipatedTournamentResponseDTO {
    private String secondPlayerName;

    public FinishedParticipatedGroupTournamentResponseDTO(String tournamentName, String secondPlayerName){
        super(tournamentName);
        this.secondPlayerName = secondPlayerName;
    }
}
