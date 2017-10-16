package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GetPageAndModifyDataDTO {
    private String[] namesOfObjectsToModify;

    private GetPageObjectsDTO getPageObjectsDTO;

    public PageRequest unwrapPageRequest() {
        return this.getPageObjectsDTO.unwrapPageRequest();
    }

}
