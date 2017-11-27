package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

public class InvalidPasswordException extends RuntimeException{
    public InvalidPasswordException(){
        super("Password incorrect");
    }
}
