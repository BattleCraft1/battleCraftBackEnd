package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.oneEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.oneEnitity.interfaces.GameService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Game.GameRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Game.GameResponseDTO;

@RestController
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @PostMapping(value = "/add/game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GameResponseDTO addGame(@RequestBody GameRequestDTO gameRequestDTO, BindingResult bindingResult){
        return gameService.addGame(gameRequestDTO, bindingResult);
    }

    @PostMapping(value = "/edit/game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GameResponseDTO editTournament(@RequestBody GameRequestDTO gameRequestDTO, BindingResult bindingResult){
        return gameService.editGame(gameRequestDTO, bindingResult);
    }

    @GetMapping(value = "/get/game")
    public GameResponseDTO getTournament(@RequestParam(value = "name") String name){
        return gameService.getGame(name);
    }
}
