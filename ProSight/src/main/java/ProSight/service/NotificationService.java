package ProSight.service;

import ProSight.entity.Task;
import ProSight.entity.User;
import ProSight.repository.TaskRepository;
import ProSight.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Deadline emails for the people doing the work:
 *  - an email when a task is assigned to someone
 *  - a daily digest of each person's overdue and upcoming tasks
 * Only goes to people who added an email address and haven't switched emails off.
 */
@Service
public class NotificationService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final DateTimeFormatter NICE_DATE = DateTimeFormatter.ofPattern("EEE d MMM");
    /** Tasks due within this many days are included in the daily digest. */
    private static final int SOON_DAYS = 3;

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final EmailService emailService;

    @Value("${prosight.app-url:http://localhost:5173}")
    private String appUrl;

    public NotificationService(UserRepository userRepository, TaskRepository taskRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.emailService = emailService;
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    private Optional<User> reachable(String username) {
        if (username == null || username.isBlank()) return Optional.empty();
        return userRepository.findByUsername(username)
                .filter(u -> isValidEmail(u.getEmail()) && u.isNotifyByEmail());
    }

    private static LocalDate deadlineOf(Task task) {
        try {
            return task.getDeadline() == null ? null : LocalDate.parse(task.getDeadline());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String projectName(Task task) {
        return task.getProject() != null ? task.getProject().getName() : "No project";
    }

    private String boardLink(Task task) {
        return task.getProject() != null ? appUrl + "/projects?id=" + task.getProject().getId() : appUrl + "/projects";
    }

    /** Email the assignee when a task is given to them. Skipped if they assigned it to themselves. */
    public void notifyAssigned(Task task, String assignedBy) {
        if (task.getAssignee() == null || task.getAssignee().equals(assignedBy)) return;
        if ("Completed".equalsIgnoreCase(task.getStatus())) return;
        reachable(task.getAssignee()).ifPresent(user -> {
            LocalDate due = deadlineOf(task);
            String dueText = due == null ? "no deadline set" : "due " + due.format(NICE_DATE) + describeDays(due);
            String body = "Hi " + user.getUsername() + ",\n\n"
                    + (assignedBy != null ? assignedBy : "Someone") + " assigned you a task in ProSight:\n\n"
                    + "  " + task.getTitle() + "\n"
                    + "  Project: " + projectName(task) + "\n"
                    + "  Priority: " + (task.getPriority() == null ? "-" : task.getPriority().toLowerCase()) + "\n"
                    + "  Deadline: " + dueText + "\n"
                    + (task.getDescription() != null && !task.getDescription().isBlank() ? "\n" + task.getDescription() + "\n" : "")
                    + "\nOpen the board: " + boardLink(task) + "\n\n"
                    + "You can turn these emails off in ProSight under Notifications.";
            emailService.sendAsync(user.getEmail(), "ProSight: you've been assigned \"" + task.getTitle() + "\"", body);
        });
    }

    /** One email per person listing their overdue tasks and tasks due in the next few days. Returns emails sent. */
    public int sendPersonalDigests() {
        return sendPersonalDigests(null);
    }

    /** Same digest, but only for one person (used by the "send it now" button). Returns emails sent (0 or 1). */
    public int sendDigestFor(String username) {
        return sendPersonalDigests(username);
    }

    private int sendPersonalDigests(String onlyUser) {
        LocalDate today = LocalDate.now();
        Map<String, List<Task>> byPerson = new HashMap<>();
        for (Task task : taskRepository.findAll()) {
            if (task.getAssignee() == null || "Completed".equalsIgnoreCase(task.getStatus())) continue;
            if (onlyUser != null && !onlyUser.equals(task.getAssignee())) continue;
            LocalDate due = deadlineOf(task);
            if (due == null || due.isAfter(today.plusDays(SOON_DAYS))) continue;
            byPerson.computeIfAbsent(task.getAssignee(), k -> new ArrayList<>()).add(task);
        }

        int sent = 0;
        for (Map.Entry<String, List<Task>> entry : byPerson.entrySet()) {
            Optional<User> user = reachable(entry.getKey());
            if (user.isEmpty()) continue;
            List<Task> tasks = entry.getValue();
            tasks.sort((a, b) -> deadlineOf(a).compareTo(deadlineOf(b)));

            List<String> overdue = new ArrayList<>(), dueToday = new ArrayList<>(), upcoming = new ArrayList<>();
            for (Task t : tasks) {
                LocalDate due = deadlineOf(t);
                String line = "  - " + t.getTitle() + " (" + projectName(t) + ", " + due.format(NICE_DATE) + describeDays(due) + ")";
                if (due.isBefore(today)) overdue.add(line);
                else if (due.isEqual(today)) dueToday.add(line);
                else upcoming.add(line);
            }

            StringBuilder body = new StringBuilder("Hi ").append(user.get().getUsername()).append(",\n\nHere are your ProSight deadlines for ")
                    .append(today.format(NICE_DATE)).append(".\n\n");
            if (!overdue.isEmpty()) body.append("Overdue:\n").append(String.join("\n", overdue)).append("\n\n");
            if (!dueToday.isEmpty()) body.append("Due today:\n").append(String.join("\n", dueToday)).append("\n\n");
            if (!upcoming.isEmpty()) body.append("Coming up in the next ").append(SOON_DAYS).append(" days:\n").append(String.join("\n", upcoming)).append("\n\n");
            body.append("Open ProSight: ").append(appUrl).append("\n\nYou can turn these emails off in ProSight under Notifications.");

            List<String> parts = new ArrayList<>();
            if (!overdue.isEmpty()) parts.add(overdue.size() + " overdue");
            if (!dueToday.isEmpty()) parts.add(dueToday.size() + " due today");
            if (!upcoming.isEmpty()) parts.add(upcoming.size() + " coming up");

            emailService.sendAsync(user.get().getEmail(), "ProSight: " + String.join(", ", parts), body.toString());
            sent++;
        }
        return sent;
    }

    /** Sends right away (not in the background) so the caller can report success or the exact error. */
    public void sendTestEmail(User user) {
        emailService.send(user.getEmail(), "ProSight test email",
                "Hi " + user.getUsername() + ",\n\nEmail notifications are working. You'll get an email when a task is "
                        + "assigned to you, and a morning summary when you have overdue tasks or deadlines in the next "
                        + SOON_DAYS + " days.\n\n- ProSight");
    }

    private static String describeDays(LocalDate due) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), due);
        if (days < -1) return ", " + (-days) + " days overdue";
        if (days == -1) return ", 1 day overdue";
        if (days == 0) return ", today";
        if (days == 1) return ", tomorrow";
        return ", in " + days + " days";
    }
}
