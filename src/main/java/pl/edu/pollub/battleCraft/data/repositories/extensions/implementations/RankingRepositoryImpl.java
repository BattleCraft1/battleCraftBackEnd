package pl.edu.pollub.battleCraft.data.repositories.extensions.implementations;

import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.RankingRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Alias;
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
                        new Field("name", "playerName", Projections::property),
                        new Field("email", "playerEmail",Projections::property),
                        new Field("participatedTournaments.id", "participatedTournamentsNumber",Projections::countDistinct),
                        new Field("plays.id", "numberOfBattles",Projections::countDistinct),
                        new Field("plays.points", "battlesPoints",Projections::sum)
                )
                .createAliases(
                        new Alias("plays", "plays"),
                        new Alias("participatedTournaments", "participatedTournaments")
                )
                .from(Player.class)
                .where(searchCriteria)
                .groupBy("id")
                .execute(requestedPage);
    }
}
