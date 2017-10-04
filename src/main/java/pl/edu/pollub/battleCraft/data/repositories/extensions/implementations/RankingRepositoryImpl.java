package pl.edu.pollub.battleCraft.data.repositories.extensions.implementations;

import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Battle.Battle;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.RankingRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Join;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces.GetPageAssistant;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class RankingRepositoryImpl implements RankingRepository{

    private final GetPageAssistant getPageAssistant;

    @Autowired
    public RankingRepositoryImpl(GetPageAssistant getPageAssistant) {
        this.getPageAssistant = getPageAssistant;
    }

    @Override
    @Transactional
    public Page getPageOfRanking(List<SearchCriteria> searchCriteria, Pageable requestedPage) {
        return getPageAssistant
                .select(
                        new Field("player.name", "name"),
                        new Field("address.city", "city"),
                        new Field("province.location", "province"),
                        new Field("tournament.id", "numberOfTournaments",Projections::countDistinct),
                        new Field("id", "numberOfBattles",Projections::countDistinct),
                        new Field("players.points", "points", Projections::sum)
                )
                .join(
                        new Join( "players", "players"),
                        new Join( "players.player", "player"),
                        new Join("player.address", "address"),
                        new Join("address.province", "province"),
                        new Join( "tour", "tour"),
                        new Join( "tour.tournament", "tournament"),
                        new Join( "tournament.game", "game")
                )
                .from(Battle.class)
                .where(searchCriteria)
                .groupBy("player.id","player.name","player.email","address.city", "province.location")
                .execute("player.name",requestedPage);
    }
}
