package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

import org.springframework.security.core.AuthenticationException;

public class AnyRoleNotFoundException extends AuthenticationException {
    public AnyRoleNotFoundException(){
        super("Any role for account not found");
    }
}
