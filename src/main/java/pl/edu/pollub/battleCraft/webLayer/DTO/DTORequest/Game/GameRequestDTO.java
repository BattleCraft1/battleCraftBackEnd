package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameRequestDTO {
    private String name;
    private String nameChange;
}
