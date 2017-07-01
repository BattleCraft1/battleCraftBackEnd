package pl.edu.pollub.battleCraft.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.exceptions.PageNotFoundException;
import pl.edu.pollub.battleCraft.services.TournamentService;


@RestController
public class TournamentController {
    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/")
    public Page<Tournament> getTournamentsFromPage(Pageable page) throws PageNotFoundException, IllegalAccessException {
        return tournamentService.getTournamentsFromPage(page);
    }
}
