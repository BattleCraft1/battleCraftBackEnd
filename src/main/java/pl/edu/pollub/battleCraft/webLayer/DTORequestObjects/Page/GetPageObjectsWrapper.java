package pl.edu.pollub.battleCraft.webLayer.DTORequestObjects.Page;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.searchSpecyficators.SearchCriteria;

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
