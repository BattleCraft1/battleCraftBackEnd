package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

public class YouAreNotOwnerOfThisObjectException extends RuntimeException{
    public YouAreNotOwnerOfThisObjectException(Class objectClass,String objectName){
        super(new StringBuilder("You are not owner of: ").append(objectClass.getSimpleName()).append(" with name: ").append(objectName).toString());
    }

    public YouAreNotOwnerOfThisObjectException(String objectType,String objectName){
        super(new StringBuilder("You are not owner of: ").append(objectType).append(" with name: ").append(objectName).toString());
    }

    public YouAreNotOwnerOfThisObjectException(String msg){
        super(msg);
    }
}
