package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UnCheckedExceptions.File;

public class FileSearchedByRelatedEntityNameNotFound extends Exception{
    public FileSearchedByRelatedEntityNameNotFound(String entityName){
        super(new StringBuilder("File for entity with name: ").append(entityName).append(" not found").toString());
    }
}
