package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.TournamentProgress.Group.Points;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayersGroupWithPointsDTO {
    private List<String> playersInGroupNames;
    private int points;
}
