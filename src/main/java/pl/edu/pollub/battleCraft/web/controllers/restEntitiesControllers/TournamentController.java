package pl.edu.pollub.battleCraft.web.controllers.restEntitiesControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.service.services.interfaces.TournamentService;
import pl.edu.pollub.battleCraft.web.jsonModels.GetPageObjectsWrapper;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
public class TournamentController {

    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @PostMapping(value = "/page/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper){
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/ban/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void banTournaments(@RequestBody String... tournamentsToBanUniqueNames){
        tournamentService.banTournaments(tournamentsToBanUniqueNames);
    }

    @PostMapping(value = "/unlock/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void unlockTournaments(@RequestBody String... tournamentsToUnlockUniqueNames){
        tournamentService.unlockTournaments(tournamentsToUnlockUniqueNames);
    }

    @PostMapping(value = "/delete/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTournaments(@RequestBody String... tournamentsToDeleteUniqueNames){
        tournamentService.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    @PostMapping(value = "/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void acceptTournaments(@RequestBody String... tournamentsToAcceptUniqueNames){
        tournamentService.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    @PostMapping(value = "/cancel/accept/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void cancelAcceptTournaments(@RequestBody String... tournamentsToCancelAcceptUniqueNames){
        tournamentService.cancelAcceptTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
