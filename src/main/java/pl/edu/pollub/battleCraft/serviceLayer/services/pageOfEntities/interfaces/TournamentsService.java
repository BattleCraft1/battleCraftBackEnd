package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;

import java.util.List;

public interface TournamentsService {
    Page getPageOfTournaments(Pageable pageable, List<SearchCriteria> searchCriteria);

    void banTournaments(String... tournamentsToBanUniqueNames);

    void unlockTournaments(String... tournamentsToUnlockUniqueNames);

    void deleteTournaments(String... tournamentsToDeleteUniqueNames);

    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames);
}
