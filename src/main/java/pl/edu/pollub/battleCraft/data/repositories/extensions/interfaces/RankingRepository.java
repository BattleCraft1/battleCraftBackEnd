package pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface RankingRepository {
    Page getPageOfRanking(List<SearchCriteria> searchCriteria, Pageable requestedPage);
}
