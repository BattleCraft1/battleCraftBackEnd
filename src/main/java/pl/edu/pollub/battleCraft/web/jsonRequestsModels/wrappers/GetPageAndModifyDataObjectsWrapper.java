package pl.edu.pollub.battleCraft.web.jsonRequestsModels.wrappers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GetPageAndModifyDataObjectsWrapper {
    private String[] namesOfObjectsToModify;

    private GetPageObjectsWrapper getPageObjectsWrapper;

    public PageRequest unwrapPageRequest() {
        return this.getPageObjectsWrapper.unwrapPageRequest();
    }
}
