package pl.edu.pollub.battleCraft.web.jsonModels;

import org.springframework.data.domain.PageRequest;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.searchCritieria.SearchCriteria;

import java.util.List;

public class GetPageAndModifyDataObjectsWrapper {
    private String[] namesOfObjectsToModify;

    private GetPageObjectsWrapper getPageObjectsWrapper;

    public void setNamesOfObjectsToModify(String... namesOfObjectsToModify){
        this.namesOfObjectsToModify=namesOfObjectsToModify;
    }

    public String[] getNamesOfObjectsToModify(){
        return namesOfObjectsToModify;
    }

    public GetPageObjectsWrapper getGetPageObjectsWrapper() {
        return getPageObjectsWrapper;
    }

    public void setGetPageObjectsWrapper(GetPageObjectsWrapper getPageObjectsWrapper) {
        this.getPageObjectsWrapper = getPageObjectsWrapper;
    }
}
