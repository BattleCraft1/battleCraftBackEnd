package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

public interface TournamentService {
    Page getPageOfTournaments(Pageable pageable, List<SearchCriteria> searchCriteria);
    void banTournaments(String... tournamentsToBanUniqueNames);
    void unlockTournaments(String... tournamentsToBanUniqueNames);
    void deleteTournaments(String... tournamentsToDeleteUniqueNames);
    void acceptTournaments(String... tournamentsToDeleteUniqueNames);
    void cancelAcceptTournaments(String... tournamentsToDeleteUniqueNames);
    List<String> getAllTournamentStatus();
}
