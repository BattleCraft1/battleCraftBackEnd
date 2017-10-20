package pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.implementations;

import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.interfaces.RankingRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.interfaces.SearchAssistant;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class RankingRepositoryImpl implements RankingRepository{

    private final SearchAssistant getPageAssistant;

    @Autowired
    public RankingRepositoryImpl(SearchAssistant getPageAssistant) {
        this.getPageAssistant = getPageAssistant;
    }

    @Override
    @Transactional
    public Page getPageOfRanking(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return getPageAssistant
                .select(
                        new Field("player.name", "name"),
                        new Field("playerAddress.city", "playerCity"),
                        new Field("playerAddress.province", "playerProvince"),
                        new Field("tournament.id", "numberOfTournaments",Projections::countDistinct),
                        new Field("id", "numberOfBattles",Projections::countDistinct),
                        new Field("players.points", "points", Projections::sum)
                )
                .join(
                        new Join( "players", "players"),
                        new Join( "players.player", "player"),
                        new Join("player.address", "playerAddress"),
                        new Join( "tour", "tour"),
                        new Join( "tour.tournament", "tournament"),
                        new Join( "tournament.address", "address"),
                        new Join( "tournament.game", "game")
                )
                .from(Battle.class)
                .where(searchCriteria)
                .groupBy("player.id","player.name","player.email","playerAddress.city","playerAddress.province")
                .execute("player.name",requestedPage);
    }
}
