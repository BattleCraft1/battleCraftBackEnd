package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities;

public class AnyObjectNotFoundException extends RuntimeException {
    public AnyObjectNotFoundException() {
        super("Any objects does not match to this search criteria");
    }
}
