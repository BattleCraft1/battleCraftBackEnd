package pl.edu.pollub.battleCraft.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.exceptions.PageNotFoundException;

public interface TournamentService {
    Page<Tournament> getTournamentsFromPage(Pageable pageable) throws PageNotFoundException, IllegalAccessException;
}
