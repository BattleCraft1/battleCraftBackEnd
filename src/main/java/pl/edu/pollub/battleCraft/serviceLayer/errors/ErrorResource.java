package pl.edu.pollub.battleCraft.serviceLayer.errors;

import java.util.HashMap;
import java.util.Map;

public class ErrorResource {
    public String message;
    public Map<String,String> fieldErrors = new HashMap<>();

    public ErrorResource(String message, Map<String,String> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

}
