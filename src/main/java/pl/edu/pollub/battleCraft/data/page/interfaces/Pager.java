package pl.edu.pollub.battleCraft.data.page.interfaces;

import org.hibernate.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Pager<V> {
    Page createPage(Long countOfSuitableEntities, Criteria criteria, Pageable requestedPage);
}
