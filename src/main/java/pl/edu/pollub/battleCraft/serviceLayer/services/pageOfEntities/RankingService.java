package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.RankingRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class RankingService {

    private final RankingRepository rankingRepository;

    public RankingService(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    public Page getPageOfRanking(Pageable pageable, List<SearchCriteria> searchCriteria) {
        searchCriteria.add(
                new SearchCriteria(
                        Arrays.asList("turn", "tournament", "banned"),
                        ":",
                        Collections.singletonList(false)
                )
        );
        searchCriteria.add(
                new SearchCriteria(
                        Arrays.asList("turn", "tournament", "status"),
                        ":",
                        Collections.singletonList("FINISHED")
                )
        );
        return rankingRepository.getPageOfRanking(searchCriteria,pageable);
    }
}
