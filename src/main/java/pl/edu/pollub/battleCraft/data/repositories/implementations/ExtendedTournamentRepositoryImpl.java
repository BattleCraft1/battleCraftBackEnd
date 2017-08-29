package pl.edu.pollub.battleCraft.data.repositories.implementations;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.entities.Tournament;
import pl.edu.pollub.battleCraft.data.page.implementations.PagerImpl;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.TournamentRepository;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedTournamentRepository;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

@Component
public class ExtendedTournamentRepositoryImpl implements ExtendedTournamentRepository{

    private final PagerImpl<Tournament> pager;
    private final TournamentRepository tournamentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ExtendedTournamentRepositoryImpl(TournamentRepository accountRepository) {
        this.pager = new PagerImpl<>(Tournament.class);
        this.tournamentRepository = accountRepository;
    }

    @Override
    @Transactional
    public Page getPageOfTournaments(SearchSpecification<Tournament> searchSpecification, Pageable requestedPage) {
        Session hibernateSession = (Session)entityManager.getDelegate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tournament> criteriaQuery = criteriaBuilder.createQuery(Tournament.class);
        Root<Tournament> tournamentRoot = criteriaQuery.from(Tournament.class);

        Criteria criteria = hibernateSession.createCriteria(Tournament.class,"tournament");

        criteria.createAlias("tournament.participants", "participants");
        criteria.createAlias("tournament.address", "address");
        criteria.createAlias("address.province", "province");
        criteria.createAlias("tournament.game", "game");

        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("tournament.name"), "name")
                .add(Projections.property("tournament.playersNumber"), "playersNumber")
                .add(Projections.property("tournament.freeSlots"), "freeSlots")
                .add(Projections.property("tournament.maxPlayers"), "maxPlayers")
                .add(Projections.property("tournament.dateOfStart"), "dateOfStart")
                .add(Projections.property("tournament.dateOfEnd"), "dateOfEnd")
                .add(Projections.property("address.city"), "city")
                .add(Projections.property("province.location"), "province")
                .add(Projections.property("game.name"), "game")
                .add(Projections.property("tournament.tournamentStatus"), "tournamentStatus")
                .add(Projections.property("tournament.banned"), "banned")
                .add(Projections.groupProperty("tournament.id"))
                .add(Projections.groupProperty("address.city"))
                .add(Projections.groupProperty("province.location"))
                .add(Projections.groupProperty("game.name"));

        criteria.setProjection(projectionList).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        return pager.createPage( tournamentRoot, searchSpecification, hibernateSession, criteria, requestedPage);
    }

    @Override
    public void banTournaments(String... tournamentsToBanUniqueNames){
        this.tournamentRepository.banTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void deleteTournaments(String... tournamentsToDeleteUniqueNames){
        this.tournamentRepository.deleteParticipationOfTournaments(tournamentsToDeleteUniqueNames);
        this.tournamentRepository.deleteTournaments(tournamentsToDeleteUniqueNames);
    }

    @Override
    public void unlockTournaments(String... tournamentsToBanUniqueNames){
        this.tournamentRepository.unlockTournaments(tournamentsToBanUniqueNames);
    }

    @Override
    public void acceptTournaments(String... tournamentsToAcceptUniqueNames) {
        this.tournamentRepository.acceptTournaments(tournamentsToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptTournaments(String... tournamentsToCancelAcceptUniqueNames) {
        this.tournamentRepository.cancelAcceptTournaments(tournamentsToCancelAcceptUniqueNames);
    }
}
