package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameRequestDTO {
    public String name;
    public String nameChange;
}
