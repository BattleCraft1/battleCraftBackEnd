package pl.edu.pollub.battleCraft.serviceLayer.services.security.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.LoginAttemptService;


/**
 * Created by Dell on 2017-03-15.
 */
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final LoginAttemptService loginAttemptService;

    @Autowired
    AuthenticationSuccessListener(final LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent e) {
        WebAuthenticationDetails auth = (WebAuthenticationDetails)
                SecurityContextHolder.getContext().getAuthentication().getDetails();

        loginAttemptService.loginSucceeded(auth.getRemoteAddress());


    }
}
