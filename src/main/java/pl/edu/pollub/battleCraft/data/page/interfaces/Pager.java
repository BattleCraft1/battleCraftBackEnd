package pl.edu.pollub.battleCraft.data.page.interfaces;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;

import javax.persistence.criteria.Root;

public interface Pager<V> {
    Page createPage(Root root, SearchSpecification searchSpecification, Session hibernateSession,
                    Criteria criteria, Pageable requestedPage);
}
