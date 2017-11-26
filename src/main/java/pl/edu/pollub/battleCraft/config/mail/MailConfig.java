package pl.edu.pollub.battleCraft.config.mail;

import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import pl.edu.pollub.battleCraft.serviceLayer.services.registration.utils.MailUtil;

import java.io.IOException;
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
        mailSender.setPassword("BattleCraft123$");
        mailSender.setUsername("battlecraftsystem@gmail.com");
        mailSender.setPort(587);
        mailSender.setProtocol("smtp");
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);
        return mailSender;
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngine(){
        VelocityEngineFactoryBean velocityEngine = new VelocityEngineFactoryBean();
        return velocityEngine;
    }

    @Bean
    public MailUtil mailService() {
        MailUtil mailService = new MailUtil();
        mailService.setMailSender(mailSender(),velocityEngine().getObject());
        return mailService;
    }

}
