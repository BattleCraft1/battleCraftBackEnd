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
import pl.edu.pollub.battleCraft.data.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.organizers.Organizer;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.data.repositories.helpers.page.implementations.PaginatorImpl;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedUserAccountRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.OrganizerRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.UserAccountRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchSpecification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExtendedUserAccountRepositoryImpl implements ExtendedUserAccountRepository {

    private final PaginatorImpl<UserAccount> pager;
    private final UserAccountRepository userAccountRepository;
    private final PlayerRepository playerRepository;
    private final OrganizerRepository organiserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ExtendedUserAccountRepositoryImpl(UserAccountRepository userAccountRepository,
                                             PlayerRepository playerRepository,
                                             OrganizerRepository organiserRepository) {
        this.playerRepository = playerRepository;
        this.organiserRepository = organiserRepository;
        this.pager = new PaginatorImpl<>(UserAccount.class);
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public Page getPageOfUserAccounts(SearchSpecification<UserAccount> searchSpecification, Pageable requestedPage) {
        Session hibernateSession = (Session) entityManager.getDelegate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserAccount> criteriaQuery = criteriaBuilder.createQuery(UserAccount.class);
        Root<UserAccount> userRoot = criteriaQuery.from(UserAccount.class);

        Criteria criteria = hibernateSession.createCriteria(UserAccount.class, "userAccount");

        criteria.createAlias("userAccount.address", "address");
        criteria.createAlias("address.province", "province");

        ProjectionList projectionList = Projections.projectionList()
                .add(Projections.property("userAccount.name"), "name")
                .add(Projections.property("userAccount.surname"), "surname")
                .add(Projections.property("userAccount.username"), "username")
                .add(Projections.property("userAccount.email"), "email")
                .add(Projections.property("userAccount.phoneNumber"), "phoneNumber")
                .add(Projections.property("address.city"), "city")
                .add(Projections.property("province.location"), "province")
                .add(Projections.property("userAccount.userType"), "userType")
                .add(Projections.property("userAccount.banned"), "banned")
                .add(Projections.groupProperty("userAccount.id"))
                .add(Projections.groupProperty("userAccount.banned"))
                .add(Projections.groupProperty("address.city"))
                .add(Projections.groupProperty("province.location"));

        criteria.setProjection(projectionList).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

        searchSpecification.toRestrictions(criteria, userRoot);

        Criteria criteriaCount = hibernateSession.createCriteria(UserAccount.class, "userAccountCount");

        criteriaCount.setProjection(Projections.countDistinct("userAccountCount.id"));

        criteriaCount.createAlias("userAccountCount.address", "address");
        criteriaCount.createAlias("address.province", "province");

        searchSpecification.toRestrictions(criteriaCount, userRoot);

        Long countOfSuitableEntities = (Long) criteriaCount.uniqueResult();

        Page result = pager.createPage(countOfSuitableEntities, criteria, requestedPage);
        return result;
    }

    @Override
    public void banUsersAccounts(String... usersAccountsToBanUniqueNames) {
        playerRepository.banUserAccounts(usersAccountsToBanUniqueNames);
    }


    @Override
    public void unlockUsersAccounts(String... usersAccountsToUnlockUniqueNames) {
        playerRepository.unlockUserAccounts(usersAccountsToUnlockUniqueNames);
    }

    @Override
    public void acceptUsersAccounts(String... usersAccountsToAcceptUniqueNames) {
        List<UserAccount> userAccountsToAccept =
                userAccountRepository.findAllUsersAccountsByUsername(usersAccountsToAcceptUniqueNames);
        List<Player> acceptedUserAccounts = this.advanceUsersToPlayers(userAccountsToAccept);
        userAccountRepository.delete(userAccountsToAccept);
        userAccountRepository.save(acceptedUserAccounts);
    }


    @Override
    public void deleteUsersAccounts(String... usersAccountsToDeleteUniqueNames) {
        userAccountRepository.deleteUserAccounts(usersAccountsToDeleteUniqueNames);
    }

    @Override
    public void cancelAcceptUsersAccounts(String... usersAccountsToCancelAcceptUniqueNames) {
        playerRepository.cancelAcceptUsersAccounts(usersAccountsToCancelAcceptUniqueNames);
    }

    @Override
    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        List<Player> playersToAdvance =
                playerRepository.findAllPlayersByUsername(playersToAdvanceToOrganizersUniqueNames);
        List<Organizer> organizers = this.advancePlayersToOrganizers(playersToAdvance);
        playerRepository.delete(playersToAdvance);
        organiserRepository.save(organizers);
    }

    @Override
    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        organiserRepository.degradeOrganizerToPlayers(organizerToDegradeToPlayersUniqueNames);
    }

    private List<Player> advanceUsersToPlayers(List<UserAccount> userAccounts){
        List<Player> players = new ArrayList<>();
        userAccounts.forEach(
                userAccount -> {
                    players.add(new Player(userAccount));
                }
        );
        return players;
    }

    private List<Organizer> advancePlayersToOrganizers(List<Player> players){
        List<Organizer> organizers = new ArrayList<>();
        players.forEach(
                player -> {
                    organizers.add(new Organizer(player));
                }
        );
        return organizers;
    }
}
