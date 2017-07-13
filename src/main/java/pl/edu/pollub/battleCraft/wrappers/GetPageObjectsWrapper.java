package pl.edu.pollub.battleCraft.wrappers;

import org.springframework.data.domain.PageRequest;
import pl.edu.pollub.battleCraft.searchSpecyfications.searchCritieria.SearchCriteria;

import java.util.List;

public class GetPageObjectsWrapper {

    private PageRequestWrapper pageRequest;

    private List<SearchCriteria> searchCriteria;

    public PageRequestWrapper getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequestWrapper pageRequestWrapper) {
        this.pageRequest = pageRequestWrapper;
    }

    public List<SearchCriteria> getSearchCriteria() {
        return searchCriteria;
    }

    public void setSearchCriteria(List<SearchCriteria> searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    public PageRequest unwrapPageRequest(){
        return pageRequest.getPageRequest();
    }
}
