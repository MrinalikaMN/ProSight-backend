package ProSight.component;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fills an EMPTY database with three sample projects and their tasks.
 * If the database already has any project at all, it does nothing,
 * so restarting the backend never creates duplicates.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DatabaseSeeder(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        long existing = projectRepository.count();
        if (existing > 0) {
            System.out.println(">>> DATABASE ALREADY HAS " + existing + " PROJECTS. SKIPPING SEEDER. <<<");
            return;
        }

        Project ecommerce = project("E-Commerce Platform Redesign", 10000.0);
        task(ecommerce, "Migrate Legacy CSS to Tailwind",
                "Convert old stylesheets to utility-first Tailwind classes for improved design consistency and faster page load speeds.",
                "To Do", "MEDIUM", "Frontend Dev", 950.0, "2026-11-08");
        task(ecommerce, "Security Audit for Payment Gateway",
                "Conduct vulnerability scans and penetration testing on checkout endpoints to ensure PCI-DSS compliance.",
                "To Do", "CRITICAL", "Security Engineer", 3200.0, "2026-10-30");
        task(ecommerce, "OAuth2 SSO Integration",
                "Implement secure third-party login providers (Google, GitHub) using Spring Security and JWT tokens.",
                "In Progress", "CRITICAL", "Backend Developer", 1500.0, "2026-10-28");
        task(ecommerce, "Implement Redis Caching for Product Catalog",
                "Cache frequently queried product listings in Redis to drastically reduce database load during traffic spikes.",
                "In Progress", "HIGH", "Backend Developer", 1800.0, "2026-11-02");
        task(ecommerce, "Employee Avatar Upload Component",
                "Build secure file upload utility allowing team members to update and store profile pictures.",
                "Completed", "LOW", "Frontend Dev", 450.0, "2026-10-20");

        Project banking = project("Mobile Banking App Redesign", 12500.0);
        task(banking, "Biometric Authentication Setup",
                "Integrate fingerprint and FaceID biometric verification APIs for secure mobile login sessions.",
                "To Do", "CRITICAL", "Security Engineer", 2800.0, "2026-11-15");
        task(banking, "Account Balance & Spending Graph",
                "Build a dynamic Recharts visualization dashboard tracking weekly and monthly user expenditures.",
                "To Do", "MEDIUM", "Frontend Dev", 1200.0, "2026-11-20");
        task(banking, "Push Notification Gateway",
                "Set up Firebase Cloud Messaging (FCM) service to alert users instantly about transaction activities.",
                "In Progress", "HIGH", "Backend Developer", 2100.0, "2026-11-10");
        task(banking, "Login & Registration Screen",
                "Implement responsive input fields, form validation, and clean UI branding for user onboarding.",
                "Completed", "LOW", "Frontend Dev", 800.0, "2026-10-25");

        Project devops = project("Cloud DevOps Automation Platform", 15000.0);
        task(devops, "Kubernetes Cluster Autoscaling",
                "Configure Horizontal Pod Autoscalers (HPA) in production clusters to dynamically handle traffic variations.",
                "To Do", "HIGH", "DevOps Engineer", 3500.0, "2026-11-25");
        task(devops, "Real-time Log Streaming UI",
                "Build an interactive frontend interface connecting to WebSocket streams for live server log monitoring.",
                "To Do", "MEDIUM", "Frontend Dev", 1800.0, "2026-11-30");
        task(devops, "Automated CI/CD Pipeline Setup",
                "Establish automated build, test, and container deployment scripts via GitHub Actions and Docker.",
                "In Progress", "CRITICAL", "DevOps Engineer", 4000.0, "2026-11-05");
        task(devops, "Cloud Infrastructure Monitoring Agent",
                "Deploy background monitoring daemons to track CPU, memory thresholds, and database health metrics.",
                "Completed", "LOW", "Backend Developer", 1500.0, "2026-10-18");

        System.out.println(">>> SEEDER FINISHED SUCCESSFULLY! 3 SAMPLE PROJECTS SAVED. <<<");
    }

    private Project project(String name, double budget) {
        Project p = new Project();
        p.setName(name);
        p.setStatus("In Progress");
        p.setBudget(budget);
        return projectRepository.save(p);
    }

    private void task(Project project, String title, String description, String status,
                      String priority, String role, double cost, String deadline) {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription(description);
        t.setStatus(status);
        t.setPriority(priority);
        t.setRole(role);
        t.setCost(cost);
        t.setDeadline(deadline);
        t.setProject(project);
        taskRepository.save(t);
    }
}