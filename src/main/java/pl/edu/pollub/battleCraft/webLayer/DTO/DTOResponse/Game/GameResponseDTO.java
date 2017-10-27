package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game;


import lombok.*;
import pl.edu.pollub.battleCraft.dataLayer.entities.Game.Game;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameResponseDTO {

    public GameResponseDTO(Game game){
        this.name = game.getName();
        this.nameChange = game.getName();
        this.creatorName = game.getCreator().getName();
        this.dateOfCreation = game.getDateOfCreation();
        this.tournamentsNumber = game.getTournamentsNumber();

        if(game.isBanned())
            this.status = "BANNED";
        else
            this.status = game.getStatus().name();

    }

    public String name;
    public String nameChange;
    public String creatorName;
    public String status;
    public Date dateOfCreation;
    public int tournamentsNumber;
}
