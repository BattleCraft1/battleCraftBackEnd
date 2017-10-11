package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityValidation;

import org.springframework.validation.Errors;

public class EntityValidationException extends RuntimeException{
    private Errors errors;

    public EntityValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
