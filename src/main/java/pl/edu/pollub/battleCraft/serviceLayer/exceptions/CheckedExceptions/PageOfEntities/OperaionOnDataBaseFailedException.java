package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities;

public class OperaionOnDataBaseFailedException extends RuntimeException{
    public OperaionOnDataBaseFailedException(String message){
        super(message);
    }
}
