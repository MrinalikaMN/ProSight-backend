package ProSight.service;

import ProSight.entity.Task;
import ProSight.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Every day at 9:00 (server time):
 *  1. emails each person who has an email address a list of THEIR overdue and due-soon tasks;
 *  2. if ALERTS_EMAIL is set, emails that address an overview of all overdue / due-tomorrow tasks.
 */
@Component
public class TaskAlertScheduler {

    private final TaskRepository taskRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Value("${prosight.alerts.email:}")
    private String alertsEmail;

    public TaskAlertScheduler(TaskRepository taskRepository, EmailService emailService, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${prosight.alerts.cron:0 0 9 * * *}")
    public void sendDailyDigest() {
        // 1. Each person gets their own overdue / due-soon tasks (if they added an email).
        try {
            int sent = notificationService.sendPersonalDigests();
            if (sent > 0) System.out.println(">>> Sent " + sent + " personal deadline emails <<<");
        } catch (Exception e) {
            System.out.println(">>> Personal deadline emails failed: " + e.getMessage() + " <<<");
        }

        // 2. Optional overview for a manager/admin address (ALERTS_EMAIL).
        if (alertsEmail == null || alertsEmail.isBlank()) {
            return;
        }

        LocalDate today = LocalDate.now();
        List<String> overdue = new ArrayList<>();
        List<String> dueSoon = new ArrayList<>();

        for (Task task : taskRepository.findAll()) {
            if (task.getDeadline() == null || "Completed".equalsIgnoreCase(task.getStatus())) continue;
            LocalDate deadline;
            try {
                deadline = LocalDate.parse(task.getDeadline());
            } catch (DateTimeParseException e) {
                continue; // skip tasks with a badly formatted date instead of crashing
            }
            String project = task.getProject() != null ? task.getProject().getName() : "No project";
            String who = task.getAssignee() != null ? ", assigned to " + task.getAssignee() : "";
            String line = "- " + task.getTitle() + " (" + project + who + ", due " + deadline + ")";
            if (deadline.isBefore(today)) overdue.add(line);
            else if (!deadline.isAfter(today.plusDays(1))) dueSoon.add(line);
        }

        if (overdue.isEmpty() && dueSoon.isEmpty()) return;

        StringBuilder body = new StringBuilder("ProSight early warnings for ").append(today).append("\n\n");
        if (!overdue.isEmpty()) body.append("Overdue (").append(overdue.size()).append("):\n").append(String.join("\n", overdue)).append("\n\n");
        if (!dueSoon.isEmpty()) body.append("Due today or tomorrow (").append(dueSoon.size()).append("):\n").append(String.join("\n", dueSoon)).append("\n\n");
        body.append("Open ProSight to update these tasks.");

        try {
            emailService.send(alertsEmail,
                    "ProSight: " + overdue.size() + " overdue, " + dueSoon.size() + " due soon",
                    body.toString());
            System.out.println(">>> Daily alert email sent to " + alertsEmail + " <<<");
        } catch (Exception e) {
            System.out.println(">>> Could not send alert email: " + e.getMessage() + " <<<");
        }
    }
}
