package ProSight.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Sends from the account configured in spring.mail.username
    @Value("${spring.mail.username:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    /** Sends in the background so a slow mail server never slows down the request. Failures are logged. */
    public void sendAsync(String to, String subject, String text) {
        CompletableFuture.runAsync(() -> {
            try {
                send(to, subject, text);
                System.out.println(">>> Email sent to " + to + ": " + subject + " <<<");
            } catch (Exception e) {
                System.out.println(">>> Could not send email to " + to + ": " + e.getMessage() + " <<<");
            }
        });
    }

    public void sendDeadlineAlert(String recipientEmail, String taskName, String dueDate) {
        send(recipientEmail, "ProSight Alert: Task Deadline Approaching",
                "Automated Reminder:\n\nYour assigned task '" + taskName + "' is due on " + dueDate
                        + ".\n\nPlease log in to ProSight to update its status or request an extension.\n\n- ProSight");
    }
}
