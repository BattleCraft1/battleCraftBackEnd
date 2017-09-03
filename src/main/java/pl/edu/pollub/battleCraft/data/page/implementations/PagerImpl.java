package pl.edu.pollub.battleCraft.data.page.implementations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.edu.pollub.battleCraft.data.page.PageImpl;
import pl.edu.pollub.battleCraft.data.page.interfaces.Pager;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PagerImpl<V> implements Pager<V> {

    public Class<V> pagerClass;

    public PagerImpl(Class<V> pagerClass) {
        this.pagerClass = pagerClass;
    }

    public PageImpl createPage(Long countOfSuitableEntities, Criteria criteria, Pageable requestedPage) {
        requestedPage.getSort().forEach(order -> {
            criteria.addOrder(order.getDirection() == Sort.Direction.ASC ? Order.asc(order.getProperty()) : Order.desc(order.getProperty()));
        });
        int requestedPageNumber = requestedPage.getPageNumber();
        int requestedPageSize = requestedPage.getPageSize();
        if (requestedPageSize > countOfSuitableEntities) {
            requestedPageSize = (int) (long) countOfSuitableEntities;
        }
        if (requestedPageNumber > countOfSuitableEntities / requestedPageSize - 1) {
            requestedPageNumber = (int) (long) countOfSuitableEntities / requestedPageSize - 1;
        }
        int firstResultNumber = requestedPageNumber * requestedPageSize;

        criteria.setFirstResult(firstResultNumber);
        criteria.setMaxResults(requestedPageSize);

        List<Map<String, Object>> result = criteria.list();

        Pageable changedPageRequest = new PageRequest(requestedPageNumber, requestedPageSize, requestedPage.getSort());

        return new PageImpl<>(result, countOfSuitableEntities, changedPageRequest);
    }
}
