package pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers;

import lombok.*;
import org.springframework.data.domain.PageRequest;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GetPageObjectsWrapper {

    private PageRequestWrapper pageRequest;

    private List<SearchCriteria> searchCriteria;

    public PageRequest unwrapPageRequest() {
        return this.pageRequest.unwrapPageRequest();
    }

}
