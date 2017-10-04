package pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Join;
import pl.edu.pollub.battleCraft.data.repositories.helpers.repositoryAssistent.field.Field;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;

import java.util.List;

public interface GetPageAssistant {
    GetPageAssistant select(Field... fields);
    GetPageAssistant join(Join... alias);
    GetPageAssistant from(Class entityClass);
    GetPageAssistant where(List<SearchCriteria> searchCriteria);
    GetPageAssistant groupBy(String... groupByFileds);
    Page execute(String countProperty,Pageable requestedPage);
}
