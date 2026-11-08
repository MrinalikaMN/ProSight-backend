package ProSight.controller;

import ProSight.entity.User;
import ProSight.repository.UserRepository;
import ProSight.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The signed-in person's own notification settings. */
@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public UserController(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    private Optional<User> currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? Optional.empty() : userRepository.findByUsername(auth.getName());
    }

    private static Map<String, Object> view(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("role", u.getRole());
        m.put("email", u.getEmail() == null ? "" : u.getEmail());
        m.put("notifyByEmail", u.isNotifyByEmail());
        return m;
    }

    @GetMapping
    public ResponseEntity<?> me() {
        return currentUser().<ResponseEntity<?>>map(u -> ResponseEntity.ok(view(u)))
                .orElse(ResponseEntity.status(401).body(Map.of("message", "Sign in again.")));
    }

    // Body: { "email": "name@example.com", "notifyByEmail": true }  (send "" to remove the email)
    @PutMapping
    public ResponseEntity<?> update(@RequestBody Map<String, Object> changes) {
        Optional<User> found = currentUser();
        if (found.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Sign in again."));
        User user = found.get();

        if (changes.get("email") instanceof String email) {
            String trimmed = email.trim();
            if (!trimmed.isEmpty() && !NotificationService.isValidEmail(trimmed)) {
                return ResponseEntity.badRequest().body(Map.of("message", "That email address doesn't look right."));
            }
            user.setEmail(trimmed.isEmpty() ? null : trimmed);
        }
        if (changes.get("notifyByEmail") instanceof Boolean on) user.setNotifyByEmail(on);

        return ResponseEntity.ok(view(userRepository.save(user)));
    }

    @PostMapping("/test-email")
    public ResponseEntity<?> testEmail() {
        Optional<User> found = currentUser();
        if (found.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Sign in again."));
        User user = found.get();
        if (!NotificationService.isValidEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Add your email address first."));
        }
        try {
            notificationService.sendTestEmail(user);
            return ResponseEntity.ok(Map.of("message", "Test email sent to " + user.getEmail()));
        } catch (Exception e) {
            // Most often: wrong Gmail app password in spring.mail.password, or no internet.
            String reason = String.valueOf(e.getMessage());
            String message = reason.toLowerCase().contains("authentication")
                    ? "Gmail rejected the login. spring.mail.password must be a 16-letter app password (all lowercase, no spaces), "
                    + "not your normal password. Check the Email check line in the IntelliJ Run window."
                    : "The mail server refused the email: " + reason;
            return ResponseEntity.status(502).body(Map.of("message", message));
        }
    }

    /** Sends the signed-in person's deadline summary right now instead of waiting for 9:00. */
    @PostMapping("/digest-now")
    public ResponseEntity<?> digestNow() {
        Optional<User> found = currentUser();
        if (found.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Sign in again."));
        User user = found.get();
        if (!NotificationService.isValidEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Add your email address first."));
        }
        if (!user.isNotifyByEmail()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Turn deadline emails on first."));
        }
        int sent = notificationService.sendDigestFor(user.getUsername());
        return ResponseEntity.ok(Map.of("message", sent > 0
                ? "Deadline summary sent to " + user.getEmail() + ". It can take a minute to arrive."
                : "Nothing to send: you have no overdue tasks or deadlines in the next 3 days."));
    }
}