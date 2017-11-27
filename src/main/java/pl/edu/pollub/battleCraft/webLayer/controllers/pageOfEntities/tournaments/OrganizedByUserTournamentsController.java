package pl.edu.pollub.battleCraft.webLayer.controllers.pageOfEntities.tournaments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.TournamentsService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageAndModifyDataDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageObjectsDTO;

@RestController
public class OrganizedByUserTournamentsController {

    private final TournamentsService tournamentService;

    @Autowired
    public OrganizedByUserTournamentsController(TournamentsService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER')")
    @PostMapping(value = "/page/organized/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page getPageOfTournaments(@RequestBody GetPageObjectsDTO getPageObjectsDTO) {
        System.out.println("Try to get tournaments");
        return tournamentService.getPageOfTournamentsOrganizedByUser(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }

    @PreAuthorize("hasAnyRole('ROLE_ORGANIZER')")
    @PostMapping(value = "/delete/organized/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page deleteTournaments(@RequestBody GetPageAndModifyDataDTO getPageAndModifyDataDTO) {
        tournamentService.deleteTournaments(getPageAndModifyDataDTO.getNamesOfObjectsToModify());
        GetPageObjectsDTO getPageObjectsDTO = getPageAndModifyDataDTO.getGetPageObjectsDTO();
        return tournamentService.getPageOfTournamentsOrganizedByUser(getPageObjectsDTO.unwrapPageRequest(),
                getPageObjectsDTO.getSearchCriteria());
    }
}
