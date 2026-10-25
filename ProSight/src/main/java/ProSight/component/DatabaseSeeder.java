package ProSight.component;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DatabaseSeeder(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only inject data if the database is completely empty
        if (projectRepository.count() == 0) {

            // Seed Project 1
            Project p1 = new Project();
            p1.setName("E-Commerce Platform Redesign");
            p1.setStatus("In Progress");
            p1.setBudget(15000.0);
            projectRepository.save(p1);

            Task t1 = new Task();
            t1.setName("Design Checkout Flow");
            t1.setDescription("Create wireframes for the new checkout process.");
            t1.setStatus("In Progress");
            t1.setPriority("High");
            t1.setAssignee("Frontend Developer");
            t1.setCost(1200.0);
            t1.setDueDate(LocalDate.now().plusDays(5));
            t1.setProject(p1);
            taskRepository.save(t1);

            Task t2 = new Task();
            t2.setName("Setup Payment Gateway");
            t2.setDescription("Integrate Stripe API for payments.");
            t2.setStatus("To Do");
            t2.setPriority("Critical");
            t2.setAssignee("Backend Developer");
            t2.setCost(2500.0);
            t2.setDueDate(LocalDate.now().plusDays(10));
            t2.setProject(p1);
            taskRepository.save(t2);

            // Seed Project 2
            Project p2 = new Project();
            p2.setName("Internal HR Dashboard");
            p2.setStatus("Completed");
            p2.setBudget(8000.0);
            projectRepository.save(p2);

            Task t3 = new Task();
            t3.setName("Employee PTO Logic");
            t3.setDescription("Calculate time off accrual.");
            t3.setStatus("Completed");
            t3.setPriority("Medium");
            t3.setAssignee("Backend Developer");
            t3.setCost(800.0);
            t3.setDueDate(LocalDate.now().minusDays(2));
            t3.setProject(p2);
            taskRepository.save(t3);

            System.out.println("Database successfully seeded with new financial fields.");
        }
    }
}