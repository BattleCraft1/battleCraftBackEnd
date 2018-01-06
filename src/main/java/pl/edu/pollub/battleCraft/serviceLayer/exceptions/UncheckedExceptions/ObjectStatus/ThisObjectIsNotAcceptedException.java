package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus;

public class ThisObjectIsNotAcceptedException extends RuntimeException{
    public ThisObjectIsNotAcceptedException(Class classOfEntity, String uniqueName){
        super(new StringBuilder(classOfEntity.getSimpleName()).append(" with name ").append(uniqueName).append(" is not accepted").toString());
    }
}
