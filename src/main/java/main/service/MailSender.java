package main.service;

import lombok.AllArgsConstructor;
import main.config.MailConfig;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailSender {

    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    public void sendMessage(String email, String title, String text) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(mailConfig.getUsername());
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject(title);
            simpleMailMessage.setText(text);
            mailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
