package pl.edu.pollub.battleCraft.serviceLayer.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.repositories.helpers.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities.AnyEntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities.PageNotFoundException;

import java.util.List;
import java.util.logging.Logger;


@Aspect
@Component
public class GetPageOfEntityAspect {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Before("execution(* pl.edu.pollub.battleCraft.serviceLayer.services.pageOfEntities.implementations.*ServiceImpl.getPageOf*(..)) && args(pageRequest,..)")
    public void checkPageSize(PageRequest pageRequest) throws IllegalAccessException {
        logger.info("checking page size");
        int allowedPageSize = 20;
        if (pageRequest.getPageSize() > allowedPageSize)
            throw new IllegalAccessException("Your page must have less than 10 elements");
    }

    @AfterReturning(pointcut = "execution(* pl.edu.pollub.battleCraft.dataLayer.repositories.pageOfEntity.implementations.*RepositoryImpl.*(..)) " +
            "&& args(searchCriteria,pageRequest)", returning = "fetchedPage",
            argNames = "searchCriteria,pageRequest,fetchedPage")
    public void checkPageContent(List<SearchCriteria> searchCriteria,
                                 PageRequest pageRequest, Page fetchedPage) throws PageNotFoundException {
        logger.info("checking page content");
        if (!fetchedPage.hasContent()) {
            if (searchCriteria.size() > 0)
                throw new AnyEntityNotFoundException();
            else
                throw new PageNotFoundException(pageRequest.getPageNumber() + 1);
        }
    }
}
