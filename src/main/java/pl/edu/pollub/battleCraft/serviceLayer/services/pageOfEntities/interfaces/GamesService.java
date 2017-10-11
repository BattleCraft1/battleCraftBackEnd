package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;

import org.springframework.core.io.Resource;

import java.util.List;

public interface GamesService {

    Page getPageOfGames(Pageable pageable, List<SearchCriteria> searchCriteria);

    void banGames(String... gamesToBanUniqueNames);

    void unlockGames(String... gamesToUnlockUniqueNames);

    void deleteGames(String... gamesToDeleteUniqueNames);

    void acceptGames(String... gamesToDeleteUniqueNames);

    void cancelAcceptGames(String... gamesToDeleteUniqueNames);
}
