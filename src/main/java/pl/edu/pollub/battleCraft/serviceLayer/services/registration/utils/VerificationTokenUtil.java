package pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.VerificationTokenRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken.VerificationToken;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Registration.VerificationException;

import java.util.Calendar;
import java.util.UUID;

@Component
public class VerificationTokenUtil {

    private static final int EXPIRATION = 60 * 24;

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenUtil(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public String createVerificationToken(UserAccount user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user, EXPIRATION);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public VerificationToken generateNewVerificationToken(UserAccount user) {
        VerificationToken token=user.getToken();
        token.setToken(UUID.randomUUID().toString());
        token.refreshDate(EXPIRATION);
        return verificationTokenRepository.save(token);
    }

    public VerificationToken getTokenObject(String token){
        return verificationTokenRepository.findByToken(token);
    }

    public void checkIfTokenIsExpired(VerificationToken verificationToken){
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getDate() - cal.getTime().getTime()) <= 0) {
            throw new VerificationException("Token is expired, please try to resend it");
        }
    }

    public void removeToken(VerificationToken verificationToken) {
        this.verificationTokenRepository.delete(verificationToken);
    }
}
