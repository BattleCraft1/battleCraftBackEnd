package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.PageOfEntities;

public class AnyEntityNotFoundException extends RuntimeException {
    public AnyEntityNotFoundException() {
        super("Any entity does not match to this search criteria");
    }
}
