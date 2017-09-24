package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.service.services.interfaces.GameService;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageAndModifyDataObjectsWrapper;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers.GetPageObjectsWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/get/allGames/names")
    public List<String> getAllGamesClassesNames() {
        return gameService.getAllGamesNames();
    }

    @PostMapping(value = "/page/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper) {
        System.out.println("Try to get games");
        return gameService.getPageOfGames(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/ban/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        gameService.banGames(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return gameService.getPageOfGames(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/unlock/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        gameService.unlockGames(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return gameService.getPageOfGames(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/delete/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        gameService.deleteGames(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return gameService.getPageOfGames(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/accept/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        gameService.acceptGames(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return gameService.getPageOfGames(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/cancel/accept/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        gameService.cancelAcceptGames(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return gameService.getPageOfGames(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @GetMapping(value = "/get/game/{gameName}/rules")
    public ResponseEntity<Resource> downloadFile(@PathVariable String gameName){
        Resource file = gameService.getGameRules(gameName);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+file.getFilename())
                .body(file);
    }

    @GetMapping("/get/games/enums")
    public Map<String, List<String>> getTournamentsEnums() {
        System.out.println("get tournament enums");
        List<String> gamesStatus = gameService.getAllGamesStatus();
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("gamesStatus", gamesStatus);
        return enums;
    }
}
