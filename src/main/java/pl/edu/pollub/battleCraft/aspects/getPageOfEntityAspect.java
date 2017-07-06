package pl.edu.pollub.battleCraft.aspects;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.exceptions.PageNotFoundException;


@Aspect
@Component
public class getPageOfEntityAspect {

    @Before("execution(* pl.edu.pollub.battleCraft.services.implementations.*ServiceImpl.getPageOf*(..)) && args(pageRequest,..)")
    public void checkPageSize(PageRequest pageRequest) throws IllegalAccessException {
        int allowedPageSize = 20;
        if(pageRequest.getPageSize()> allowedPageSize)
            throw new IllegalAccessException("Your page must have less than 10 elements");
    }

    @AfterReturning(pointcut="execution(* pl.edu.pollub.battleCraft.repositories.*Repository.*(..)) && args(*,pageRequest)", returning="fetchedPage", argNames = "pageRequest,fetchedPage")
    public void checkPageContent(PageRequest pageRequest, Page fetchedPage) throws PageNotFoundException {
        if(!fetchedPage.hasContent())
            throw new PageNotFoundException(pageRequest.getPageNumber());
    }
}
