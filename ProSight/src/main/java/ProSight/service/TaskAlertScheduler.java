package ProSight.service;

import ProSight.entity.Task;
import ProSight.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TaskAlertScheduler {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    public TaskAlertScheduler(TaskRepository taskRepository, EmailService emailService) {
        this.taskRepository = taskRepository;
        this.emailService = emailService;
    }

    // Runs every 60,000 milliseconds (1 minute)
    @Scheduled(fixedRate = 60000)
    public void checkUpcomingDeadlines() {
        List<Task> allTasks = taskRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Task task : allTasks) {
            if (task.getDueDate() != null && !task.getStatus().equals("Completed")) {
                if (task.getDueDate().equals(today)) {

                    // Replace with your actual email address
                    String adminEmail = "your.email@gmail.com";

                    // Convert LocalDate to String to fix the compiler error
                    emailService.sendDeadlineAlert(adminEmail, task.getName(), task.getDueDate().toString());
                    System.out.println("Sent deadline alert for task: " + task.getName());
                }
            }
        }
    }
}