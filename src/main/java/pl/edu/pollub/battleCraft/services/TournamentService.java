package pl.edu.pollub.battleCraft.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;

import java.util.List;

public interface TournamentService {
    Page getPageOfTournaments(Pageable pageable, List<SearchCriteria> searchCriteria);
    void banTournaments(List<String> tournamentsToBanUniqueNames);
    void unlockTournaments(List<String> tournamentsToBanUniqueNames);
    void deleteTournaments(List<String> tournamentsToDeleteUniqueNames);
}
