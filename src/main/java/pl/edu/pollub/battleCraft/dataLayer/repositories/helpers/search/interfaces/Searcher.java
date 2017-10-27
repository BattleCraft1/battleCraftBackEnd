package pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;

import java.util.List;

public interface Searcher {
    Searcher select(Field... fields);
    Searcher join(Join... alias);
    Searcher from(Class entityClass);
    Searcher where(List<SearchCriteria> searchCriteria);
    Searcher groupBy(String... groupByFileds);
    Page execute(String countProperty,Pageable requestedPage);
}
