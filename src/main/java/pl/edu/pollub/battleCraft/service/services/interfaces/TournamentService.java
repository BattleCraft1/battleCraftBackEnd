package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface TournamentService {
    Page getPageOfTournaments(Pageable pageable, List<SearchCriteria> searchCriteria);

    void banTournaments(String... tournamentsToBanUniqueNames);

    void unlockTournaments(String... tournamentsToUnlockUniqueNames);

    void deleteTournaments(String... tournamentsToDeleteUniqueNames);

    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames);

    List<String> getAllTournamentStatus();
}
