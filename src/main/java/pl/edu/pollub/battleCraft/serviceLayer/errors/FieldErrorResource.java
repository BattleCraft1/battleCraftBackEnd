package pl.edu.pollub.battleCraft.serviceLayer.errors;

public class FieldErrorResource {
    public String field;
    public String message;

    public FieldErrorResource(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
