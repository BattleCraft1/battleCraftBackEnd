package pl.edu.pollub.battleCraft.webLayer.errorsHandlers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.edu.pollub.battleCraft.serviceLayer.errors.ErrorResource;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsNotAcceptedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisTournamentIsAlreadyStarted;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.AnyObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.OperationOnPageFailedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.PageNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.StorageException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.StorageFileNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.UserAvatar.InvalidUserAvatarExtension;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration.VerificationException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.InvalidPasswordException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.MyAccessDeniedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.YouAreNotOwnerOfThisObjectException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.TournamentManagement.TournamentManagementException;

import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {PageNotFoundException.class,
                                ObjectNotFoundException.class,
                                StorageFileNotFoundException.class,
                                AnyObjectNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ResponseEntity<String> handleNotFoundException(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {OperationOnPageFailedException.class, InvalidUserAvatarExtension.class, TournamentManagementException.class,
            ThisObjectIsNotAcceptedException.class, ThisObjectIsNotAcceptedException.class,
            AuthenticationException.class, VerificationException.class, ThisTournamentIsAlreadyStarted.class,
            YouAreNotOwnerOfThisObjectException.class, ThisObjectIsBannedException.class, InvalidPasswordException.class})

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ResponseEntity<String> handleBadRequest(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccessDeniedException.class, MyAccessDeniedException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<String> handleUnauthorizedRequest(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
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


    @ExceptionHandler(value = {RuntimeException.class, Exception.class, StorageException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity<String> handleInternalException(Exception ex, WebRequest req) {
        System.out.println("exception: "+ex.getClass().getSimpleName()+" message: " + ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>("There are unrecognized problems on the server side. Please contact with administrator.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
