package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

public class InvalidPasswordConfirmationException extends RuntimeException{
    public InvalidPasswordConfirmationException(){
        super("Password confirmation and password are not the same");
    }
}
