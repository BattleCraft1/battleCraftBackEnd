package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(Class classOfEntity, String uniqueName){
        super(new StringBuilder(classOfEntity.getSimpleName()).append(" with name ").append(uniqueName).append(" not found").toString());
    }
    public ObjectNotFoundException(Class classOfEntity){
        super(new StringBuilder(classOfEntity.getSimpleName()).append(" not found").toString());
    }
}
