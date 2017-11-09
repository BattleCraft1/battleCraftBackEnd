package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities;

public class OperationOnPageFailedException extends RuntimeException{
    public OperationOnPageFailedException(String message){
        super(message);
    }
}
