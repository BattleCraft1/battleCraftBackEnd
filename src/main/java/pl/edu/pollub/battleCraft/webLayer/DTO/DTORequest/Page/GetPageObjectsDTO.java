package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GetPageObjectsDTO {

    private PageRequestDTO pageRequest;

    private List<SearchCriteria> searchCriteria;

    public PageRequest unwrapPageRequest() {
        return this.pageRequest.unwrapPageRequest();
    }

    public void setPageRequest(PageRequestDTO pageRequest){
        this.pageRequest = pageRequest;
    }

}
