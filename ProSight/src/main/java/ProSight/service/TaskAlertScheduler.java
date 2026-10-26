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
            if (task.getDeadline() != null && !"Completed".equalsIgnoreCase(task.getStatus())) {
                LocalDate deadlineDate = LocalDate.parse(task.getDeadline());
                if (deadlineDate.equals(today)) {
                    String adminEmail = "your.email@gmail.com";
                    // Add your email notification logic here using emailService if needed
                }
            }
        }
    }
}