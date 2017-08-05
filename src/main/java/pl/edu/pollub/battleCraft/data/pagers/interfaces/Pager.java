package pl.edu.pollub.battleCraft.data.pagers.interfaces;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.function.Consumer;

public interface Pager<V> {
    public PageImpl createPage(Session hibernateSession, Criteria criteria, Pageable requestedPage);
}
