package pl.edu.pollub.battleCraft.service.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface RankingService {
    Page getPageOfRanking(Pageable pageable, List<SearchCriteria> searchCriteria);
}
