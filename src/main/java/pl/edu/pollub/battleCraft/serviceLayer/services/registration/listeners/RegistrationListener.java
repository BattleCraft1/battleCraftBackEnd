package pl.edu.pollub.battleCraft.serviceLayer.services.registration.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration.VerificationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.events.OnRegistrationCompleteEvent;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.VerificationTokenUtil;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final VerificationTokenUtil verificationTokenUtil;

    private final MailUtil mailUtil;

    @Autowired
    public RegistrationListener(VerificationTokenUtil verificationTokenUtil, MailUtil mailUtil) {
        this.verificationTokenUtil = verificationTokenUtil;
        this.mailUtil = mailUtil;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserAccount user = event.getUser();
        String token = verificationTokenUtil.createVerificationToken(user);

        try {
            mailUtil.sendVerificationMail(user.getEmail(),token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new VerificationException("There are unrecognized problems with sending verification e-mail. Please try to execute verification process again.");
        }
    }
}
