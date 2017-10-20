package pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.RankingRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.interfaces.RankingService;

import java.util.List;

@Service
public class RankingServiceImpl implements RankingService{

    private final RankingRepository rankingRepository;

    public RankingServiceImpl(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    @Override
    public Page getPageOfRanking(Pageable pageable, List<SearchCriteria> searchCriteria) {
        return rankingRepository.getPageOfRanking(searchCriteria,pageable);
    }
}
