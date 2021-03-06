package pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity;

import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.Searcher;
import pl.edu.pollub.battleCraft.dataLayer.domain.Battle.Battle;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.AnyObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.PageNotFoundException;

import java.util.List;

@Component
public class RankingRepository{

    private final Searcher searcher;

    @Autowired
    public RankingRepository(Searcher searcher) {
        this.searcher = searcher;
    }

    @Transactional(rollbackFor = {AnyObjectNotFoundException.class,PageNotFoundException.class})
    public Page getPageOfRanking(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return searcher
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
                        new Join("player.addressOwnership.address", "playerAddress"),
                        new Join( "turn", "turn"),
                        new Join( "turn.tournament", "tournament"),
                        new Join( "tournament.addressOwnership.address", "address"),
                        new Join( "tournament.game", "game")
                )
                .from(Battle.class)
                .where(searchCriteria)
                .groupBy("player.id","player.name","player.email","playerAddress.city","playerAddress.province")
                .execute("player.name",requestedPage);
    }
}
