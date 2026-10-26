package ProSight.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendDeadlineAlert(String recipientEmail, String taskName, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();

        // This should match the email address you put in application.properties
        message.setFrom("your.email@gmail.com");
        message.setTo(recipientEmail);
        message.setSubject("ProSight Alert: Task Deadline Approaching");
        message.setText("Automated Reminder:\n\nYour assigned task '" + taskName + "' is due on " + dueDate + ".\n\nPlease log in to the ProSight Kanban board to update its status or request an extension.\n\n- ProSight System");

        mailSender.send(message);
    }
}