package pl.edu.pollub.battleCraft.service.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.data.searchSpecyficators.SearchSpecification;
import pl.edu.pollub.battleCraft.service.exceptions.AnyEntityNotFoundException;
import pl.edu.pollub.battleCraft.service.exceptions.PageNotFoundException;

import java.util.logging.Logger;


@Aspect
@Component
public class GetPageOfEntityAspect {

    private int allowedPageSize = 20;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Before("execution(* pl.edu.pollub.battleCraft.service.services.implementations.*ServiceImpl.getPageOf*(..)) && args(pageRequest,..)")
    public void checkPageSize(PageRequest pageRequest) throws IllegalAccessException {
        logger.info("checking page size");
        if(pageRequest.getPageSize()> allowedPageSize)
            throw new IllegalAccessException("Your page must have less than 10 elements");
    }

    @AfterReturning(pointcut="execution(* pl.edu.pollub.battleCraft.data.repositories.extensions.*Repository.*(..)) " +
            "&& args(objectSearchSpecification,pageRequest)", returning="fetchedPage",
            argNames = "objectSearchSpecification,pageRequest,fetchedPage")
    public void checkPageContent(SearchSpecification<?> objectSearchSpecification,
                                 PageRequest pageRequest, Page fetchedPage) throws PageNotFoundException {
        logger.info("checking page content");
        if(!fetchedPage.hasContent()) {
            if(objectSearchSpecification.getCriteries().size()>0)
                throw new AnyEntityNotFoundException();
            else
                throw new PageNotFoundException(pageRequest.getPageNumber()+1);
        }
    }

    public int getAllowedPageSize() {
        return allowedPageSize;
    }

    public void setAllowedPageSize(int allowedPageSize) {
        this.allowedPageSize = allowedPageSize;
    }
}
