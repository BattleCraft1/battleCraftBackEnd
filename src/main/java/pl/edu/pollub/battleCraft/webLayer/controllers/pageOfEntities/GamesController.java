package pl.edu.pollub.battleCraft.webLayer.controllers.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.GamesService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.GameResourcesService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageAndModifyDataDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageObjectsDTO;

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
    public Page getPageOfTournaments(@RequestBody GetPageObjectsDTO getPageObjectsDTO) {
        System.out.println("Try to get games");
        return gameService.getPageOfGames(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/ban/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        gameService.banGames(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return gameService.getPageOfGames(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/unlock/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        gameService.unlockGames(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return gameService.getPageOfGames(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/delete/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        gameService.deleteGames(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        gameResourcesService.deleteGamesRules(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return gameService.getPageOfGames(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/accept/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        gameService.acceptGames(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return gameService.getPageOfGames(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/cancel/accept/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        gameService.cancelAcceptGames(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return gameService.getPageOfGames(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }
}
