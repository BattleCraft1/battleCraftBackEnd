package pl.edu.pollub.battleCraft.data.repositories.helpers.page.interfaces;

import org.hibernate.Criteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Paginator<V> {
    Page createPage(Long countOfSuitableEntities, Criteria criteria, Pageable requestedPage);
}
