package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game.GameResponseDTO;

@Component
public class GameToResponseDTOMapper {
    public GameResponseDTO map(Game game){
        GameResponseDTO gameResponseDTO = new GameResponseDTO();

        gameResponseDTO.setName( game.getName());
        gameResponseDTO.setNameChange(game.getName());
        gameResponseDTO.setCreatorName(game.getCreator().getName());
        gameResponseDTO.setDateOfCreation(game.getDateOfCreation());
        gameResponseDTO.setTournamentsNumber(game.getTournamentsNumber());

        if(game.isBanned())
            gameResponseDTO.setStatus("BANNED");
        else
            gameResponseDTO.setStatus(game.getStatus().name());
        return gameResponseDTO;
    }
}
