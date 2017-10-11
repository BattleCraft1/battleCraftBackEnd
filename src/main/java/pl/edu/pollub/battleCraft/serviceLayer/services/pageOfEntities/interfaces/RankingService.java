package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface RankingService {
    Page getPageOfRanking(Pageable pageable, List<SearchCriteria> searchCriteria);
}
