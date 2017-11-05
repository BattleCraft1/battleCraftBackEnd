package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.GameEnumsService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TournamentEnumsController {

    private final GameEnumsService gameEnumsService;

    @Autowired
    public TournamentEnumsController(GameEnumsService gameEnumsService) {
        this.gameEnumsService = gameEnumsService;
    }


    @GetMapping("/get/tournaments/enums")
    public List<String> getTournamentsEnums() {
        List<String> gamesNames = gameEnumsService.getAllAcceptedGamesNames();
        return gamesNames;
    }
}
