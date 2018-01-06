package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.TournamentProgress;

import lombok.*;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlayersGroupDTO {
    List<String> playersNames;
    int playersPoints;
}
