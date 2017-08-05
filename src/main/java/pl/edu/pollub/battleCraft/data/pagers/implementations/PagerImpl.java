package pl.edu.pollub.battleCraft.data.pagers.implementations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.pagers.interfaces.Pager;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
@Setter
public class PagerImpl<V> implements Pager<V> {

    public Class<V> pagerClass;

    public PagerImpl(Class<V> pagerClass){
        this.pagerClass=pagerClass;
    }

    public PageImpl createPage(Session hibernateSession, Criteria criteria, Pageable requestedPage){
        requestedPage.getSort().forEach(order -> {
            criteria.addOrder(order.getDirection() == Sort.Direction.ASC? Order.asc(order.getProperty()):Order.desc(order.getProperty()));
        });

        Criteria criteriaCount = hibernateSession.createCriteria(pagerClass);
        criteria.setFirstResult(requestedPage.getPageNumber()*requestedPage.getPageSize());
        criteria.setMaxResults(requestedPage.getPageSize());
        criteriaCount.setProjection(Projections.rowCount());

        List<Map<String,Object>> result = criteria.list();

        return new PageImpl<>(result,requestedPage,(Long) criteriaCount.uniqueResult());
    }
}
