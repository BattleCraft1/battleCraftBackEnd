package pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.velocity.VelocityEngineUtils;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Report.ReportDTO;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MailUtil {

    private JavaMailSender mailSender;

    private VelocityEngine velocityEngine;

    public void setMailSender(JavaMailSenderImpl mailSender, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
    }

    public void sendMail(String from, String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    public void sendVerificationMail(String recipientAddress, String token) throws MessagingException {
        Map<String,Object> model = new HashMap();
        model.put("confirmationUrl","http:/localhost:8080/registration/confirm?token="+token);
        model.put("date", Calendar.getInstance().get(Calendar.YEAR));
        this.sendVelocityMail(recipientAddress,"Confirm registration", model, "verificationMailTemplate.vm");
    }

    public void sendResetPasswordEmail(String recipientAddress, String password, String username) throws MessagingException {
        Map<String,Object> model = new HashMap();
        model.put("temporaryPassword",password);
        model.put("username",username);
        model.put("date", Calendar.getInstance().get(Calendar.YEAR));
        this.sendVelocityMail(recipientAddress,"Reset password", model, "resetPasswordTemplate.vm");
    }

    public void sendRegistrationCompleteMail(String recipientAddress) throws MessagingException {
        Map<String,Object> model = new HashMap();
        model.put("date", Calendar.getInstance().get(Calendar.YEAR));
       this.sendVelocityMail(recipientAddress,"Registration Completed", model, "registrationCompletedTemplate.vm");
    }

    public void sendReportToAdmin(String userWhoReportName, ReportDTO reportDTO, String adminEmail){
        Map<String,Object> model = new HashMap();
        model.put("date", Calendar.getInstance().get(Calendar.YEAR));
        model.put("reportUserName", userWhoReportName);
        model.put("objectType", reportDTO.getObjectType());
        model.put("objectNames", String.join(", ", reportDTO.getObjectNames()));
        model.put("reportMessage", reportDTO.getReportMessage());
        this.sendVelocityMail(adminEmail,"Report", model, "reportTemplate.vm");
    }

    @Async
    void sendVelocityMail(String recipientAddress, String subject, Map<String, Object> model, String templateName){
        MimeMessagePreparator preparator = (MimeMessage mimeMessage)  -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(recipientAddress);
            message.setFrom("from@no-spam.com");
            message.setSubject(subject);
            message.setSentDate(new Date());
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                    "./src/main/resources/templates/velocity/"+templateName,
                    "UTF-8", model);
            message.setText(text, true);
        };

        mailSender.send(preparator);
    }

    public void sendMail(SimpleMailMessage message) {
        mailSender.send(message);
    }

}
