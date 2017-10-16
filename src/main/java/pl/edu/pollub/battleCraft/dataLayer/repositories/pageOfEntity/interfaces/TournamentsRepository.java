package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;


public interface TournamentsRepository {
    Page getPageOfTournaments(List<SearchCriteria> searchCriteria, Pageable requestedPage);

    void banTournaments(String... tournamentsToBanUniqueNames);

    void deleteTournaments(String... tournamentsToDeleteUniqueNames);

    void unlockTournaments(String... tournamentsToBanUniqueNames);

    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    void cancelAcceptationTournaments(String... tournamentsToCancelAcceptUniqueNames);

    void deleteTournamentInProgressionRelations(List<Long> idsOfToursToDelete);
}
