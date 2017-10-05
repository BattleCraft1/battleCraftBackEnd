package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.service.services.interfaces.GameService;
import pl.edu.pollub.battleCraft.service.services.interfaces.ProvinceService;
import pl.edu.pollub.battleCraft.service.services.interfaces.RankingService;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageObjectsWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RankingController {

    private final RankingService rankingService;

    private final ProvinceService provinceService;

    private final GameService gameService;

    @Autowired
    public RankingController(RankingService rankingService, ProvinceService provinceService, GameService gameService) {
        this.rankingService = rankingService;
        this.provinceService = provinceService;
        this.gameService = gameService;
    }

    @PostMapping(value = "/page/ranking", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper) {
        System.out.println("Try to get rankings");
        return rankingService.getPageOfRanking(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @GetMapping("/get/ranking/enums")
    public Map<String, List<String>> getTournamentsEnums() {
        List<String> provincesNames = provinceService.getAllProvincesNames();
        List<String> gamesNames = gameService.getAllGamesNames();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("provincesNames", provincesNames);
        enums.put("gamesNames", gamesNames);
        return enums;
    }
}
