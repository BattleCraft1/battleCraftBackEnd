package pl.edu.pollub.battleCraft.data.repositories.extensions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.entities.Tournament;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;


public interface ExtendedTournamentRepository {
    Page getPageOfTournaments(SearchSpecification<Tournament> objectSearchSpecification, Pageable requestedPage);

    void banTournaments(String... tournamentsToBanUniqueNames);

    void deleteTournaments(String... tournamentsToDeleteUniqueNames);

    void unlockTournaments(String... tournamentsToBanUniqueNames);

    void acceptTournaments(String... tournamentsToAcceptUniqueNames);

    void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames);
}
