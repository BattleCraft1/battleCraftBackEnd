package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Dell on 2017-04-14.
 */
public class TooManyLoginAttempts extends AuthenticationException {

    public TooManyLoginAttempts() {
        super("You tried to login more than 5 times. So your possibility of login is blocked for 15 minutes");
    }
}
