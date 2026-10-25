package ProSight.component;

import ProSight.entity.Project;
import ProSight.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ProjectRepository projectRepository;

    public DatabaseSeeder(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only inject data if the database is completely empty
        if (projectRepository.count() == 0) {

            Project p1 = new Project();
            p1.setName("E-Commerce Platform Redesign");
            p1.setDescription("Overhauling the frontend architecture for the customer checkout flow.");
            p1.setStatus("In Progress");
            p1.setHealth(85);
            p1.setAllocatedBudget(15000.0);
            p1.setActualSpent(8200.0);
            p1.setTargetDate(LocalDate.of(2026, 11, 30));

            Project p2 = new Project();
            p2.setName("Internal HR Dashboard");
            p2.setDescription("Employee portal for managing time-off requests and payroll summaries.");
            p2.setStatus("Completed");
            p2.setHealth(100);
            p2.setAllocatedBudget(8000.0);
            p2.setActualSpent(7150.0);
            p2.setTargetDate(LocalDate.of(2026, 10, 15));
            p2.setCompletionDate(LocalDate.of(2026, 10, 12));

            Project p3 = new Project();
            p3.setName("Mobile App MVP");
            p3.setDescription("Initial build of the customer loyalty mobile application.");
            p3.setStatus("Planning");
            p3.setHealth(95);
            p3.setAllocatedBudget(25000.0);
            p3.setActualSpent(1500.0);
            p3.setTargetDate(LocalDate.of(2027, 2, 28));

            projectRepository.saveAll(Arrays.asList(p1, p2, p3));
            System.out.println("Pseudo-data successfully injected into the database.");
        }
    }
}