package pl.edu.pollub.battleCraft.data.page.implementations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.edu.pollub.battleCraft.data.entities.Tournament;
import pl.edu.pollub.battleCraft.data.page.PageImpl;
import pl.edu.pollub.battleCraft.data.page.interfaces.Pager;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PagerImpl<V> implements Pager<V> {

    public Class<V> pagerClass;

    public PagerImpl(Class<V> pagerClass){
        this.pagerClass=pagerClass;
    }

    public PageImpl createPage(Root root, SearchSpecification searchSpecification, Session hibernateSession,
                               Criteria criteria, Pageable requestedPage){
        requestedPage.getSort().forEach(order -> {
            criteria.addOrder(order.getDirection() == Sort.Direction.ASC? Order.asc(order.getProperty()):Order.desc(order.getProperty()));
        });

        searchSpecification.toRestrictions(criteria, root);

        criteria.setFirstResult(requestedPage.getPageNumber()*requestedPage.getPageSize());
        criteria.setMaxResults(requestedPage.getPageSize());

        List<Map<String,Object>> result = criteria.list();

        Criteria criteriaCount = hibernateSession.createCriteria(Tournament.class,"tournamentsCount");

        criteriaCount.setProjection(Projections.countDistinct("tournamentsCount.id"));

        criteriaCount.createAlias("tournamentsCount.participants", "participants");
        criteriaCount.createAlias("tournamentsCount.address", "address");
        criteriaCount.createAlias("address.province", "province");
        criteriaCount.createAlias("tournamentsCount.game", "game");

        searchSpecification.toRestrictions(criteriaCount, root);

        Long count = (Long)criteriaCount.uniqueResult();

        return new PageImpl<>(result,count,requestedPage);
    }
}
