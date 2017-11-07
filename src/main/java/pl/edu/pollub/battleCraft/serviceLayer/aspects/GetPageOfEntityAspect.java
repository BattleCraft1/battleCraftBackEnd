package pl.edu.pollub.battleCraft.serviceLayer.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

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
}
