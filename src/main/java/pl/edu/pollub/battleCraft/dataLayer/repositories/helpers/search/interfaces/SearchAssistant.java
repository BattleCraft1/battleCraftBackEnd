package pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Join;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.field.Field;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;

import java.util.List;

public interface SearchAssistant {
    SearchAssistant select(Field... fields);
    SearchAssistant join(Join... alias);
    SearchAssistant from(Class entityClass);
    SearchAssistant where(List<SearchCriteria> searchCriteria);
    SearchAssistant groupBy(String... groupByFileds);
    Page execute(String countProperty,Pageable requestedPage);
}
