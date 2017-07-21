package pl.edu.pollub.battleCraft.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.edu.pollub.battleCraft.entities.enums.TournamentClass;
import pl.edu.pollub.battleCraft.services.TournamentService;
import pl.edu.pollub.battleCraft.wrappers.GetPageObjectsWrapper;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/get/allTournamentClasses/names")
    public List<String> getAllTournamentClassesNames(){
        return TournamentClass.getNames();
    }

    @PostMapping(value = "/page/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page getPageOfTournaments(@RequestBody GetPageObjectsWrapper getPageObjectsWrapper){
        return tournamentService.getPageOfTournaments(getPageObjectsWrapper.unwrapPageRequest(),
                getPageObjectsWrapper.getSearchCriteria());
    }

    @PostMapping(value = "/ban/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void banTournaments(@RequestBody ArrayList<String> tournamentsToBanUniqueNames){
        System.out.println(tournamentsToBanUniqueNames);
        tournamentService.banTournaments(tournamentsToBanUniqueNames);
    }

    @PostMapping(value = "/unlock/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void unlockTournaments(@RequestBody ArrayList<String> tournamentsToUnlockUniqueNames){
        System.out.println(tournamentsToUnlockUniqueNames);
        tournamentService.unlockTournaments(tournamentsToUnlockUniqueNames);
    }

    @PostMapping(value = "/delete/tournaments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTournaments(@RequestBody ArrayList<String> tournamentsToDeleteUniqueNames){
        System.out.println(tournamentsToDeleteUniqueNames);
        tournamentService.deleteTournaments(tournamentsToDeleteUniqueNames);
    }
}
