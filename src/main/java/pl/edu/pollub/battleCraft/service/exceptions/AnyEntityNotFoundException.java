package pl.edu.pollub.battleCraft.service.exceptions;

public class AnyEntityNotFoundException extends RuntimeException {
    public AnyEntityNotFoundException() {
        super("Any entity does not match to this search criteria");
    }
}
