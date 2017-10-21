package pl.edu.pollub.battleCraft.webLayer.controllers.webMechanismControllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.pollub.battleCraft.serviceLayer.errors.ErrorResource;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities.AnyEntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities.OperaionOnDataBaseFailedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities.PageNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.File.StorageException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.File.StorageFileNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.UserAvatar.InvalidUserAvatarExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {RuntimeException.class, Exception.class, StorageException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<String> handleInternalException(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>("There are unrecognized problems on the server side. Please contact with administrator.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {PageNotFoundException.class, EntityNotFoundException.class, StorageFileNotFoundException.class, AnyEntityNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<String> handleNotFoundException(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {OperaionOnDataBaseFailedException.class, InvalidUserAvatarExtension.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ResponseEntity<String> handleBadRequest(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntityValidationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleValidationException(Exception ex, WebRequest request) {
        EntityValidationException entityValidationException = (EntityValidationException) ex;

        List<FieldError> fieldErrors = entityValidationException.getErrors().getFieldErrors();

        ErrorResource error = new ErrorResource( entityValidationException.getMessage(),
                fieldErrors.stream().collect(Collectors.toMap(FieldError::getField,
                        FieldError::getDefaultMessage))
                );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }
}
