package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

import org.springframework.security.core.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {
    public InvalidPasswordException(String msg){
        super(msg);
    }
}
