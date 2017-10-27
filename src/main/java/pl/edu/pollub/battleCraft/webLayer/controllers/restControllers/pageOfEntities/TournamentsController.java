package pl.edu.pollub.battleCraft.webLayer.controllers.restControllers.pageOfEntities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.TournamentsService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageAndModifyDataDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageObjectsDTO;

@RestController
public class TournamentsController {

    private final TournamentsService tournamentService;

    @Autowired
    public TournamentsController(TournamentsService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping(value = "/page/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page getPageOfTournaments(@RequestBody GetPageObjectsDTO getPageObjectsDTO) {
        System.out.println("Try to get tournaments");
        return tournamentService.getPageOfTournaments(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PostMapping(value = "/ban/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page banTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        tournamentService.banTournaments(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return tournamentService.getPageOfTournaments(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PostMapping(value = "/unlock/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page unlockTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        tournamentService.unlockTournaments(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return tournamentService.getPageOfTournaments(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PostMapping(value = "/delete/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        tournamentService.deleteTournaments(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return tournamentService.getPageOfTournaments(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PostMapping(value = "/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page acceptTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        tournamentService.acceptTournaments(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return tournamentService.getPageOfTournaments(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PostMapping(value = "/cancel/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page cancelAcceptTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        tournamentService.cancelAcceptTournaments(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return tournamentService.getPageOfTournaments(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }
}
