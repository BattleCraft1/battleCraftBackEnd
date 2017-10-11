package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.GamesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.GameResourcesService;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page.GetPageAndModifyDataObjectsWrapper;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page.GetPageObjectsWrapper;

@RestController
public class GamesController {

    private final GamesService gameService;

    private final GameResourcesService gameResourcesService;

    @Autowired
    public GamesController(GamesService gameService, GameResourcesService gameResourcesService) {
        this.gameService = gameService;
        this.gameResourcesService = gameResourcesService;
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
        gameResourcesService.deleteGamesRules(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
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
}
