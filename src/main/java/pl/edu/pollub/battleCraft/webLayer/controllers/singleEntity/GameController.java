package pl.edu.pollub.battleCraft.webLayer.controllers.singleEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity.GameService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game.GameResponseDTO;
import pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers.GameToResponseDTOMapper;

@RestController
public class GameController {

    private final GameService gameService;

    private final GameToResponseDTOMapper gameToResponseDTOMapper;

    @Autowired
    public GameController(GameService gameService, GameToResponseDTOMapper gameToResponseDTOMapper){
        this.gameService = gameService;
        this.gameToResponseDTOMapper = gameToResponseDTOMapper;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER')")
    @PostMapping(value = "/add/game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GameResponseDTO addGame(@RequestBody GameRequestDTO gameRequestDTO, BindingResult bindingResult){
        return gameToResponseDTOMapper.map(gameService.addGame(gameRequestDTO, bindingResult));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_ORGANIZER')")
    @PostMapping(value = "/edit/game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GameResponseDTO editTournament(@RequestBody GameRequestDTO gameRequestDTO, BindingResult bindingResult){
        return gameToResponseDTOMapper.map(gameService.editGame(gameRequestDTO, bindingResult));
    }

    @GetMapping(value = "/get/game")
    public GameResponseDTO getTournament(@RequestParam(value = "name") String name){
        return gameToResponseDTOMapper.map(gameService.getGame(name));
    }
}
