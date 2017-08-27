package pl.edu.pollub.battleCraft.web.controllers.webMechanismControllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.pollub.battleCraft.service.exceptions.AnyEntityNotFoundException;
import pl.edu.pollub.battleCraft.service.exceptions.PageNotFoundException;
import pl.edu.pollub.battleCraft.service.exceptions.StorageException;
import pl.edu.pollub.battleCraft.service.exceptions.StorageFileNotFoundException;


@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class, Exception.class, StorageException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<String> handleInternalException(Exception ex, WebRequest req)
    {
        System.out.println(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {PageNotFoundException.class,StorageFileNotFoundException.class, AnyEntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<String> handleNotFoundException(Exception ex, WebRequest req)
    {
        System.out.println(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
