package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.GameEnumsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RankingEnumsController {


    private final GameEnumsService gameEnumsService;

    @Autowired
    public RankingEnumsController(GameEnumsService gameEnumsService) {
        this.gameEnumsService = gameEnumsService;
    }

    @GetMapping("/get/ranking/enums")
    public Map<String, List<String>> getTournamentsEnums() {
        List<String> gamesNames = gameEnumsService.getAllGamesNames();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("gamesNames", gamesNames);
        return enums;
    }
}
