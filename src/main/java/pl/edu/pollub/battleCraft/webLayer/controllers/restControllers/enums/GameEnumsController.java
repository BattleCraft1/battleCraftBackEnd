package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.GameEnumsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GameEnumsController {

    private final GameEnumsService gameEnumsService;

    @Autowired
    public GameEnumsController(GameEnumsService gameEnumsService) {
        this.gameEnumsService = gameEnumsService;
    }

    @GetMapping("/get/allGames/names")
    public List<String> getAllGamesClassesNames() {
        return gameEnumsService.getAllGamesNames();
    }

    @GetMapping("/get/games/enums")
    public Map<String, List<String>> getTournamentsEnums() {
        System.out.println("get tournament enums");
        List<String> gamesStatus = gameEnumsService.getAllGamesStatus();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("gamesStatus", gamesStatus);
        return enums;
    }
}
