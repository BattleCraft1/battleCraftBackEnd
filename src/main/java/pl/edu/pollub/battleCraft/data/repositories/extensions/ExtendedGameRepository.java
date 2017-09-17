package pl.edu.pollub.battleCraft.data.repositories.extensions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface ExtendedGameRepository {
    Page getPageOfGames(List<SearchCriteria> searchCriteria, Pageable requestedPage);

    void banGames(String... gamesToBanUniqueNames);

    void deleteGames(String... gamesToDeleteUniqueNames);

    void unlockGames(String... gamesToBanUniqueNames);

    void acceptGames(String... gamesToAcceptUniqueNames);

    void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames);

    List<String> getAllGamesNames();
}
