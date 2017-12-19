package pl.edu.pollub.battleCraft.serviceLayer.services.registration.events;

import org.springframework.context.ApplicationEvent;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;

import java.util.Locale;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final UserAccount user;

    public OnRegistrationCompleteEvent(UserAccount user) {
        super(user);

        this.user = user;
    }

    public UserAccount getUser() {
        return user;
    }

}
