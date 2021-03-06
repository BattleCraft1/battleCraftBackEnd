package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestDTO {

    private int size;

    private int page;

    private String direction;

    private String property;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public PageRequest unwrapPageRequest() {
        Sort.Direction direction;
        if (this.direction.equals("ASC"))
            direction = Sort.Direction.ASC;
        else
            direction = Sort.Direction.DESC;

        return new PageRequest(page, size, direction, property);
    }
}
