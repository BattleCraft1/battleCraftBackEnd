package pl.edu.pollub.battleCraft.serviceLayer.services.registration.events;

import org.springframework.context.ApplicationEvent;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;

import java.util.Locale;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private final String appUrl;
    private final Locale locale;
    private final UserAccount user;

    public OnRegistrationCompleteEvent(
            UserAccount user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public UserAccount getUser() {
        return user;
    }

}
