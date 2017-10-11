package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface RankingRepository {
    Page getPageOfRanking(List<SearchCriteria> searchCriteria, Pageable requestedPage);
}
