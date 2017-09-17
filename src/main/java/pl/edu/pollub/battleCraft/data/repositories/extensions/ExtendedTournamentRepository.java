package pl.edu.pollub.battleCraft.data.repositories.extensions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;


public interface ExtendedTournamentRepository {
    Page getPageOfTournaments(List<SearchCriteria> searchCriteria, Pageable requestedPage);

    void banTournaments(String... tournamentsToBanUniqueNames);

    void deleteTournaments(String... tournamentsToDeleteUniqueNames);

    void unlockTournaments(String... tournamentsToBanUniqueNames);

    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames);
}
