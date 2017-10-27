package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities;

public class OperaionOnDataBaseFailedException extends RuntimeException{
    public OperaionOnDataBaseFailedException(String message){
        super(message);
    }
}
