package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game;


import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class GameResponseDTO {
    private String name;
    private String nameChange;
    private String creatorName;
    private String status;
    private Date dateOfCreation;
    private int tournamentsNumber;
    private boolean canCurrentUserEdit;
}
