package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration;

public class VerificationException extends RuntimeException{
    public VerificationException(String msg){
        super(msg);
    }
}
