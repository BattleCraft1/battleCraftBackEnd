package pl.edu.pollub.battleCraft.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.entities.Tournament;
import pl.edu.pollub.battleCraft.exceptions.PageNotFoundException;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;

import java.util.List;

public interface TournamentService {
    Page<Tournament> getTournamentsFromPage(List<SearchCriteria> searchCriteria, Pageable pageable) throws PageNotFoundException, IllegalAccessException;
}
