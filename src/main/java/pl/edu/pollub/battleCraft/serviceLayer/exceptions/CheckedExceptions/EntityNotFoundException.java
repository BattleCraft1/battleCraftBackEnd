package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(Class classOfEntity, String uniqueName){
        super(new StringBuilder(classOfEntity.getSimpleName()).append(" with name ").append(uniqueName).append(" not found").toString());
    }
}
