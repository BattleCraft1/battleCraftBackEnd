package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;

import java.util.List;

public interface GamesRepository {
    Page getPageOfGames(List<SearchCriteria> searchCriteria, Pageable requestedPage);

    void banGames(String... gamesToBanUniqueNames);

    void deleteGames(String... gamesToDeleteUniqueNames);

    void unlockGames(String... gamesToBanUniqueNames);

    void acceptGames(String... gamesToAcceptUniqueNames);

    void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames);
}
