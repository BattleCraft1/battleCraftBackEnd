package pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces;

import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game.GameResponseDTO;

public interface GameService {
    GameResponseDTO addGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult);
    GameResponseDTO editGame(GameRequestDTO gameRequestDTO, BindingResult bindingResult);
    GameResponseDTO getGame(String gameUniqueName);
}
