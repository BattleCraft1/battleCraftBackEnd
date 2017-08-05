package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

public interface TournamentService {
    Page getPageOfTournaments(Pageable pageable, List<SearchCriteria> searchCriteria);
    void banTournaments(List<String> tournamentsToBanUniqueNames);
    void unlockTournaments(List<String> tournamentsToBanUniqueNames);
    void deleteTournaments(List<String> tournamentsToDeleteUniqueNames);
    void acceptTournaments(ArrayList<String> tournamentsToDeleteUniqueNames);
    void cancelAcceptTournaments(ArrayList<String> tournamentsToDeleteUniqueNames);
}
