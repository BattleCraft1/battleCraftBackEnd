package pl.edu.pollub.battleCraft.data.repositories.implementations;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Game.Game;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedGameRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.page.implementations.PaginatorImpl;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.GameRepository;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class ExtendedGameRepositoryImpl implements ExtendedGameRepository{
    private final PaginatorImpl<Game> pager;
    private final GameRepository gameRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ExtendedGameRepositoryImpl(GameRepository gameRepository) {
        this.pager = new PaginatorImpl<>(Game.class);
        this.gameRepository = gameRepository;
    }

    @Override
    public Page getPageOfGames(SearchSpecification<Game> searchSpecification, Pageable requestedPage) {
        Session hibernateSession = (Session) entityManager.getDelegate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Game> criteriaQuery = criteriaBuilder.createQuery(Game.class);
        Root<Game> gameRoot = criteriaQuery.from(Game.class);

        Criteria criteria = hibernateSession.createCriteria(Game.class, "game");

        criteria.createAlias("game.creator", "creator");

        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("game.name"), "name")
                .add(Projections.property("game.tournamentsNumber"), "tournamentsNumber")
                .add(Projections.property("creator.username"), "creatorUsername")
                .add(Projections.property("game.gameStatus"), "gameStatus")
                .add(Projections.property("game.banned"), "banned")
                .add(Projections.groupProperty("creator.username"))
                .add(Projections.groupProperty("game.id"));

        criteria.setProjection(projectionList).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        searchSpecification.toRestrictions(criteria, gameRoot);

        Criteria criteriaCount = hibernateSession.createCriteria(Game.class, "gamesCount");
        criteriaCount.setProjection(Projections.countDistinct("gamesCount.id"));
        criteriaCount.createAlias("gamesCount.creator", "creator");
        searchSpecification.toRestrictions(criteriaCount, gameRoot);
        Long countOfSuitableEntities = (Long) criteriaCount.uniqueResult();

        return pager.createPage(countOfSuitableEntities, criteria, requestedPage);
    }

    @Override
    public void banGames(String... gamesToBanUniqueNames) {
        gameRepository.banGames(gamesToBanUniqueNames);
    }

    @Override
    public void deleteGames(String... gamesToDeleteUniqueNames) {
        gameRepository.deleteGames(gamesToDeleteUniqueNames);
    }

    @Override
    public void unlockGames(String... gamesToUnlockUniqueNames) {
        gameRepository.unlockGames(gamesToUnlockUniqueNames);
    }

    @Override
    public void acceptGames(String... gamesToAcceptUniqueNames) {
        gameRepository.acceptGames(gamesToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptGames(String... gamesToCancelAcceptUniqueNames) {
        gameRepository.cancelAcceptGames(gamesToCancelAcceptUniqueNames);
    }

    @Override
    public List<String> getAllGamesNames() {
        return gameRepository.getAllGamesNames();
    }
}
