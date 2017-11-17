package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus;

public class ThisObjectIsBannedException extends RuntimeException{
    public ThisObjectIsBannedException(Class classOfEntity, String uniqueName){
        super(new StringBuilder(classOfEntity.getSimpleName()).append(" with name ").append(uniqueName).append(" is not accepted").toString());
    }
}
