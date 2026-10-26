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
        if (projectRepository.count() == 0) {
            Project p1 = new Project();
            p1.setName("E-Commerce Platform Redesign");
            p1.setStatus("In Progress");
            p1.setBudget(15000.0);
            projectRepository.save(p1);

            Task t1 = new Task();
            t1.setTitle("Design Checkout Flow");
            t1.setDescription("Create wireframes for the new checkout process.");
            t1.setStatus("In Progress");
            t1.setPriority("High");
            t1.setRole("Frontend Developer");
            t1.setCost(1200.0);
            t1.setDeadline(LocalDate.now().plusDays(5).toString());
            t1.setProject(p1);
            taskRepository.save(t1);

            Task t2 = new Task();
            t2.setTitle("Setup Payment Gateway");
            t2.setDescription("Integrate Stripe API for payments.");
            t2.setStatus("To Do");
            t2.setPriority("Critical");
            t2.setRole("Backend Developer");
            t2.setCost(2500.0);
            t2.setDeadline(LocalDate.now().plusDays(10).toString());
            t2.setProject(p1);
            taskRepository.save(t2);
        }
    }
}