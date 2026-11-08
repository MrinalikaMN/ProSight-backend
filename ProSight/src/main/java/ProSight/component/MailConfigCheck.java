package ProSight.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Prints at startup whether secrets.properties was found and whether the email settings look usable.
 * Never prints the password itself.
 */
@Component
public class MailConfigCheck {

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    @EventListener(ApplicationReadyEvent.class)
    public void check() {
        boolean inResources = getClass().getClassLoader().getResource("secrets.properties") != null;
        File workingDir = new File(System.getProperty("user.dir"));
        boolean inWorkingDir = new File(workingDir, "secrets.properties").isFile();
        String where = inResources ? "found in src/main/resources"
                : inWorkingDir ? "found in " + workingDir.getAbsolutePath()
                : "NOT FOUND. Put it in src/main/resources (the app was started from " + workingDir.getAbsolutePath() + ")";
        System.out.println(">>> secrets.properties: " + where + " <<<");
        System.out.println(">>> Email check: " + describe(username, password) + " <<<");
    }

    static String describe(String username, String password) {
        if (username == null || username.isBlank()) {
            return "spring.mail.username is empty, so emails are OFF. Add it to secrets.properties.";
        }
        if (password == null || password.isBlank()) {
            return "spring.mail.password is empty, so emails are OFF. Add your Gmail app password to secrets.properties.";
        }
        if (password.startsWith("PUT_YOUR")) {
            return "secrets.properties still has the placeholder text. Replace it with your Gmail app password.";
        }
        String noSpaces = password.replace(" ", "");
        if (!noSpaces.equals(password) && noSpaces.matches("[a-z]{16}")) {
            return "the app password contains spaces. Remove them (it should be 16 letters in a row).";
        }
        if (password.matches("[a-z]{16}")) {
            return "settings look right (" + username + ", 16-letter app password).";
        }
        if (password.length() == 16 && password.toLowerCase().matches("[a-z]{16}")) {
            return "the password has capital letters, but Gmail app passwords are all lowercase. "
                    + "Check letters that look alike, like a capital I and a lowercase l.";
        }
        return "spring.mail.password doesn't look like a Gmail app password (16 lowercase letters). "
                + "Gmail rejects normal passwords. Create one at myaccount.google.com/apppasswords.";
    }
}