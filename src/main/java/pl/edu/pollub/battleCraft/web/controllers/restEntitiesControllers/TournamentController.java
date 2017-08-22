package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.data.entities.Tournament;
import pl.edu.pollub.battleCraft.service.services.interfaces.GameService;
import pl.edu.pollub.battleCraft.service.services.interfaces.ProvinceService;
import pl.edu.pollub.battleCraft.service.services.interfaces.TournamentService;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.GetPageAndModifyDataObjectsWrapper;
import pl.edu.pollub.battleCraft.web.jsonRequestsModels.GetPageObjectsWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TournamentController {

    private final TournamentService tournamentService;

    private final ProvinceService provinceService;

    private final GameService gameService;

    @Autowired
    public TournamentController(TournamentService tournamentService,
                                ProvinceService provinceService,
                                GameService gameService) {
        this.tournamentService = tournamentService;
        this.gameService = gameService;
        this.provinceService = provinceService;
    }

    @PostMapping(value = "/page/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper){
        System.out.println("Try to get tournaments");
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/ban/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        tournamentService.banTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper=getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/unlock/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        tournamentService.unlockTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper=getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/delete/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        tournamentService.deleteTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper=getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        tournamentService.acceptTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper=getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/cancel/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper){
        tournamentService.cancelAcceptTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper=getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @GetMapping("/get/tournaments/status")
    public List<String> getAllProvincesNames(){
        return tournamentService.getAllTournamentStatus();
    }

    @GetMapping("/get/tournaments/enums")
    public Map<String,List<String>> getTournamentsEnums(){
        List<String> tournamentStatus = tournamentService.getAllTournamentStatus();
        List<String> provincesNames = provinceService.getAllProvincesNames();
        List<String> gamesNames = gameService.getAllGamesNames();
        Map<String,List<String>> enums = new HashMap<>();
        enums.put("tournamentStatus",tournamentStatus);
        enums.put("provincesNames",provincesNames);
        enums.put("gamesNames",gamesNames);
        return enums;
    }
}
