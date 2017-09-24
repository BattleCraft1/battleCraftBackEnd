package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import org.springframework.core.io.Resource;
import java.io.IOException;
import java.util.List;

public interface GameService {
    List<String> getAllGamesNames();

    Page getPageOfGames(Pageable pageable, List<SearchCriteria> searchCriteria);

    void banGames(String... gamesToBanUniqueNames);

    void unlockGames(String... gamesToUnlockUniqueNames);

    void deleteGames(String... gamesToDeleteUniqueNames);

    void acceptGames(String... gamesToDeleteUniqueNames);

    void cancelAcceptGames(String... gamesToDeleteUniqueNames);

    Resource getGameRules(String gameName);

    List<String> getAllGamesStatus();
}
