package pl.edu.pollub.battleCraft.webLayer.controllers.pageOfEntities.tournaments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.TournamentsService;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page.GetPageObjectsDTO;

@RestController
public class ParticipatedByUserTournamentsController {

    private final TournamentsService tournamentService;

    @Autowired
    public ParticipatedByUserTournamentsController(TournamentsService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PreAuthorize("hasAnyRole('ROLE_ACCEPTED','ROLE_ORGANIZER')")
    @PostMapping(value = "/page/participated/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Page getPageOfTournamentsParticipatedByUser(@RequestBody GetPageObjectsDTO getPageObjectsDTO) {
        return tournamentService.getPageOfTournamentsParticipatedByUser(getPageObjectsDTO.unwrapPageRequest(), getPageObjectsDTO.getSearchCriteria());
    }
}
