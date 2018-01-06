package pl.edu.pollub.battleCraft.serviceLayer.errors;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ErrorResource {
    private String message;
    private Map<String,String> fieldErrors = new HashMap<>();

    public ErrorResource(String message, Map<String,String> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }
}
