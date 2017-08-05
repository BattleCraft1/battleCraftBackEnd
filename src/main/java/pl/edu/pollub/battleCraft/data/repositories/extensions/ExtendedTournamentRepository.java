package pl.edu.pollub.battleCraft.data.repositories.extensions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.entities.Tournament;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

import java.util.ArrayList;
import java.util.List;

public interface ExtendedTournamentRepository {
    Page getPageOfTournaments(SearchSpecification<Tournament> objectSearchSpecification, Pageable requestedPage);

    void banTournaments(List<String> tournamentsToBanUniqueNames);

    void deleteTournaments(List<String> tournamentsToDeleteUniqueNames);

    void unlockTournaments(List<String> tournamentsToBanUniqueNames);

    void acceptTournaments(ArrayList<String> tournamentsToAcceptUniqueNames);

    void cancelAcceptTournaments(ArrayList<String> tournamentsToCancelAcceptUniqueNames);
}
