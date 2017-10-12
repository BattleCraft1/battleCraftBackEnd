package pl.edu.pollub.battleCraft.serviceLayer.errors;

import java.util.ArrayList;
import java.util.List;

public class ErrorResource {
    public String message;
    public List<FieldErrorResource> fieldErrors = new ArrayList<>();

    public ErrorResource(String message, List<FieldErrorResource> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

}
