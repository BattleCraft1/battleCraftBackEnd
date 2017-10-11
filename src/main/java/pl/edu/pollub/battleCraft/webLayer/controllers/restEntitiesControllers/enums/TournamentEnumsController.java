package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.enums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.GameEnumsService;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.TournamentEnumsService;
import pl.edu.pollub.battleCraft.serviceLayer.services.enums.interfaces.ProvinceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TournamentEnumsController {
    private final TournamentEnumsService tournamentEnumsService;

    private final ProvinceService provinceService;

    private final GameEnumsService gameEnumsService;

    @Autowired
    public TournamentEnumsController(TournamentEnumsService tournamentEnumsService, ProvinceService provinceService, GameEnumsService gameEnumsService) {
        this.tournamentEnumsService = tournamentEnumsService;
        this.provinceService = provinceService;
        this.gameEnumsService = gameEnumsService;
    }

    @GetMapping("/get/tournaments/status")
    public List<String> getAllProvincesNames() {
        return tournamentEnumsService.getAllTournamentStatus();
    }

    @GetMapping("/get/tournaments/enums")
    public Map<String, List<String>> getTournamentsEnums() {
        List<String> tournamentStatus = tournamentEnumsService.getAllTournamentStatus();
        List<String> provincesNames = provinceService.getAllProvincesNames();
        List<String> gamesNames = gameEnumsService.getAllGamesNames();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("tournamentStatus", tournamentStatus);
        enums.put("provincesNames", provincesNames);
        enums.put("gamesNames", gamesNames);
        return enums;
    }
}
