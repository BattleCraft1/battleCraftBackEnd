package pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.VerificationToken.VerificationToken;

@Component
public class MailUtil {
    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String from, String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendVerificationMail(String recipientAddress, String appUrl, String token){

        String subject = "Registration Confirmation";
        String confirmationUrl = appUrl + "/registration/confirm?token=" + token;
        String message = "Welcome to BattleCraft! Please click this link to verify your account: ";

        this.sendMail("from@no-spam.com",recipientAddress,subject,message + "http://localhost:8080" + confirmationUrl);
    }

    public void sendMail(SimpleMailMessage message) {
        mailSender.send(message);
    }

}
