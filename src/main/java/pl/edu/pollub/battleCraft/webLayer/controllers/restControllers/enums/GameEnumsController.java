package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.GameEnumsService;

import java.util.List;

@RestController
public class GameEnumsController {

    private final GameEnumsService gameEnumsService;

    @Autowired
    public GameEnumsController(GameEnumsService gameEnumsService) {
        this.gameEnumsService = gameEnumsService;
    }

    @GetMapping("/get/allGames/names")
    public List<String> getAllGamesClassesNames() {
        return gameEnumsService.getAllAcceptedGamesNames();
    }
}
