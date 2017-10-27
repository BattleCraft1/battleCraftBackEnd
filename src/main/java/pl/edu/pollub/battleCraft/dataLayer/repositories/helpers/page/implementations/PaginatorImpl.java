package pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.page.implementations;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.page.PageImpl;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.page.interfaces.Paginator;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.AnyEntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.PageNotFoundException;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PaginatorImpl<V> implements Paginator<V> {

    public Class<V> pagerClass;

    public PaginatorImpl(Class<V> pagerClass) {
        this.pagerClass = pagerClass;
    }

    public PageImpl createPage(Long countOfSuitableEntities, Criteria criteria, Pageable requestedPage) {
        if (countOfSuitableEntities == 0) {
            if (criteria.list().size() > 0)
                throw new AnyEntityNotFoundException();
            else
                throw new PageNotFoundException(requestedPage.getPageNumber() + 1);
        }

        requestedPage.getSort().forEach(order -> {
            criteria.addOrder(order.getDirection() == Sort.Direction.ASC ? Order.asc(order.getProperty()) : Order.desc(order.getProperty()));
        });
        int requestedPageNumber = requestedPage.getPageNumber();
        int requestedPageSize = requestedPage.getPageSize();
        if (requestedPageSize > countOfSuitableEntities) {
            requestedPageSize = (int) (long) countOfSuitableEntities;
        }
        if (requestedPageNumber > countOfSuitableEntities / requestedPageSize) {
            requestedPageNumber = (int) (long) countOfSuitableEntities / requestedPageSize;
        }
        int firstResultNumber = requestedPageNumber * requestedPageSize;
        criteria.setFirstResult(firstResultNumber);
        criteria.setMaxResults(requestedPageSize);

        List<Map<String, Object>> result = criteria.list();
        Pageable changedPageRequest = new PageRequest(requestedPageNumber, requestedPageSize, requestedPage.getSort());

        return new PageImpl<>(result, countOfSuitableEntities, changedPageRequest);
    }
}
