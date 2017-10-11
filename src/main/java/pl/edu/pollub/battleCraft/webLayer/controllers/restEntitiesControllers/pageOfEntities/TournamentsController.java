package pl.edu.pollub.battleCraft.webLayer.controllers.restEntitiesControllers.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.TournamentsService;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page.GetPageAndModifyDataObjectsWrapper;
import pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page.GetPageObjectsWrapper;

@RestController
public class TournamentsController {

    private final TournamentsService tournamentService;

    @Autowired
    public TournamentsController(TournamentsService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping(value = "/page/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper) {
        System.out.println("Try to get tournaments");
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/ban/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        tournamentService.banTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/unlock/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        tournamentService.unlockTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/delete/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        tournamentService.deleteTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        tournamentService.acceptTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/cancel/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptTournaments(@RequestBody GetPageAndModifyDataObjectsWrapper getPageAndModifyDataObjectsWrapper) {
        tournamentService.cancelAcceptTournaments(getPageAndModifyDataObjectsWrapper.getNamesOfObjectsToModify());
        GetPageObjectsWrapper getPageObjectsWrapper = getPageAndModifyDataObjectsWrapper.getGetPageObjectsWrapper();
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }
}
