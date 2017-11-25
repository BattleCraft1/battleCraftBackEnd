package pl.edu.pollub.battleCraft.config.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;

import java.util.Locale;
import java.util.Properties;

/**
 * Created by Dell on 2017-03-15.
 */
@Configuration
public class MailConfig {

    @Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPassword("STALKER70A3");
        mailSender.setUsername("testaccont666@gmail.com");
        mailSender.setPort(587);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

    @Bean
    public MailUtil mailService() {
        MailUtil mailService = new MailUtil();
        mailService.setMailSender(mailSender());
        return mailService;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Bean
    public SessionLocaleResolver sessionLocaleResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(new Locale("en"));
        return sessionLocaleResolver;
    }
}
