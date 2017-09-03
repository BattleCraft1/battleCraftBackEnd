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
import pl.edu.pollub.battleCraft.data.page.implementations.PagerImpl;
import pl.edu.pollub.battleCraft.data.repositories.extensions.ExtendedUserAccountRepository;
import pl.edu.pollub.battleCraft.data.repositories.interfaces.UserAccountRepository;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Component
public class ExtendedUserAccountRepositoryImpl implements ExtendedUserAccountRepository {

    private final PagerImpl<UserAccount> pager;
    private final UserAccountRepository userAccountRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ExtendedUserAccountRepositoryImpl(UserAccountRepository userAccountRepository) {
        this.pager = new PagerImpl<>(UserAccount.class);
        this.userAccountRepository = userAccountRepository;
    }

    @Override
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
                .add(Projections.groupProperty("userAccount.id"))
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

        return pager.createPage(countOfSuitableEntities, criteria, requestedPage);
    }
}
