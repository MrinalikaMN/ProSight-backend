package ProSight;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.entity.Recommendation;
import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import ProSight.repository.RecommendationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
@EnableScheduling
public class ProSightApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProSightApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:5174", "http://localhost:5173")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*");
			}
		};
	}

	@Bean
	public CommandLineRunner seedData(ProjectRepository projectRepository, TaskRepository taskRepository) {
		return args -> {
			// Only seed if no projects exist yet
			if (projectRepository.count() == 0) {

				// ===============================
				// PROJECT 1: E-Commerce Platform Redesign
				// ===============================
				Project p1 = new Project();
				p1.setName("E-Commerce Platform Redesign");
				p1.setStatus("In Progress");
				p1.setBudget(10000.0);
				projectRepository.save(p1);

				Task t1 = new Task();
				t1.setTitle("Migrate Legacy CSS to Tailwind");
				t1.setDescription("Convert old stylesheets to utility-first Tailwind classes for improved design consistency and faster page load speeds.");
				t1.setStatus("To Do");
				t1.setPriority("MEDIUM");
				t1.setRole("Frontend Dev");
				t1.setCost(950.0);
				t1.setDeadline("2026-11-08");
				t1.setProject(p1);
				taskRepository.save(t1);

				Task t2 = new Task();
				t2.setTitle("Security Audit for Payment Gateway");
				t2.setDescription("Conduct vulnerability scans and penetration testing on checkout endpoints to ensure PCI-DSS compliance.");
				t2.setStatus("To Do");
				t2.setPriority("CRITICAL");
				t2.setRole("Security Engineer");
				t2.setCost(3200.0);
				t2.setDeadline("2026-10-30");
				t2.setProject(p1);
				taskRepository.save(t2);

				Task t3 = new Task();
				t3.setTitle("OAuth2 SSO Integration");
				t3.setDescription("Implement secure third-party login providers (Google, GitHub) using Spring Security and JWT tokens.");
				t3.setStatus("In Progress");
				t3.setPriority("CRITICAL");
				t3.setRole("Backend Developer");
				t3.setCost(1500.0);
				t3.setDeadline("2026-10-28");
				t3.setProject(p1);
				taskRepository.save(t3);

				Task t4 = new Task();
				t4.setTitle("Implement Redis Caching for Product Catalog");
				t4.setDescription("Cache frequently queried product listings in Redis to drastically reduce database load during traffic spikes.");
				t4.setStatus("In Progress");
				t4.setPriority("HIGH");
				t4.setRole("Backend Developer");
				t4.setCost(1800.0);
				t4.setDeadline("2026-11-02");
				t4.setProject(p1);
				taskRepository.save(t4);

				Task t5 = new Task();
				t5.setTitle("Employee Avatar Upload Component");
				t5.setDescription("Build secure file upload utility allowing team members to update and store profile pictures.");
				t5.setStatus("Completed");
				t5.setPriority("LOW");
				t5.setRole("Frontend Developer");
				t5.setCost(450.0);
				t5.setDeadline("2026-10-20");
				t5.setProject(p1);
				taskRepository.save(t5);


				// ===============================
				// PROJECT 2: Mobile Banking App Redesign
				// ===============================
				Project p2 = new Project();
				p2.setName("Mobile Banking App Redesign");
				p2.setStatus("In Progress");
				p2.setBudget(12500.0);
				projectRepository.save(p2);

				Task t6 = new Task();
				t6.setTitle("Biometric Authentication Setup");
				t6.setDescription("Integrate fingerprint and FaceID biometric verification APIs for secure mobile login sessions.");
				t6.setStatus("To Do");
				t6.setPriority("CRITICAL");
				t6.setRole("Security Engineer");
				t6.setCost(2800.0);
				t6.setDeadline("2026-11-15");
				t6.setProject(p2);
				taskRepository.save(t6);

				Task t7 = new Task();
				t7.setTitle("Account Balance & Spending Graph");
				t7.setDescription("Build a dynamic Recharts visualization dashboard tracking weekly and monthly user expenditures.");
				t7.setStatus("To Do");
				t7.setPriority("MEDIUM");
				t7.setRole("Frontend Dev");
				t7.setCost(1200.0);
				t7.setDeadline("2026-11-20");
				t7.setProject(p2);
				taskRepository.save(t7);

				Task t8 = new Task();
				t8.setTitle("Push Notification Gateway");
				t8.setDescription("Set up Firebase Cloud Messaging (FCM) service to alert users instantly about transaction activities.");
				t8.setStatus("In Progress");
				t8.setPriority("HIGH");
				t8.setRole("Backend Developer");
				t8.setCost(2100.0);
				t8.setDeadline("2026-11-10");
				t8.setProject(p2);
				taskRepository.save(t8);

				Task t9 = new Task();
				t9.setTitle("Login & Registration Screen");
				t9.setDescription("Implement responsive input fields, form validation, and clean UI branding for user onboarding.");
				t9.setStatus("Completed");
				t9.setPriority("LOW");
				t9.setRole("Frontend Dev");
				t9.setCost(800.0);
				t9.setDeadline("2026-10-25");
				t9.setProject(p2);
				taskRepository.save(t9);


				// ===============================
				// PROJECT 3: Cloud DevOps Automation Platform
				// ===============================
				Project p3 = new Project();
				p3.setName("Cloud DevOps Automation Platform");
				p3.setStatus("In Progress");
				p3.setBudget(15000.0);
				projectRepository.save(p3);

				Task t10 = new Task();
				t10.setTitle("Kubernetes Cluster Autoscaling");
				t10.setDescription("Configure Horizontal Pod Autoscalers (HPA) in production clusters to dynamically handle traffic variations.");
				t10.setStatus("To Do");
				t10.setPriority("HIGH");
				t10.setRole("DevOps Engineer");
				t10.setCost(3500.0);
				t10.setDeadline("2026-11-25");
				t10.setProject(p3);
				taskRepository.save(t10);

				Task t11 = new Task();
				t11.setTitle("Real-time Log Streaming UI");
				t11.setDescription("Build an interactive frontend interface connecting to WebSocket streams for live server log monitoring.");
				t11.setStatus("To Do");
				t11.setPriority("MEDIUM");
				t11.setRole("Frontend Dev");
				t11.setCost(1800.0);
				t11.setDeadline("2026-11-30");
				t11.setProject(p3);
				taskRepository.save(t11);

				Task t12 = new Task();
				Task t12Instance = t12;
				t12Instance.setTitle("Automated CI/CD Pipeline Setup");
				t12Instance.setDescription("Establish automated build, test, and container deployment scripts via GitHub Actions and Docker.");
				t12Instance.setStatus("In Progress");
				t12Instance.setPriority("CRITICAL");
				t12Instance.setRole("DevOps Engineer");
				t12Instance.setCost(4000.0);
				t12Instance.setDeadline("2026-11-05");
				t12Instance.setProject(p3);
				taskRepository.save(t12Instance);

				Task t13 = new Task();
				t13.setTitle("Cloud Infrastructure Monitoring Agent");
				t13.setDescription("Deploy background monitoring daemons to track CPU, memory thresholds, and database health metrics.");
				t13.setStatus("Completed");
				t13.setPriority("LOW");
				t13.setRole("Backend Developer");
				t13.setCost(1500.0);
				t13.setDeadline("2026-10-18");
				t13.setProject(p3);
				taskRepository.save(t13);

				System.out.println(">>> FULL PROSIGHT PROJECTS & TASKS SEEDED SUCCESSFULLY! <<<");
			}
		};
	}

	@Bean
	public CommandLineRunner seedRecommendations(RecommendationRepository recommendationRepository, ProjectRepository projectRepository) {
		return args -> {
			projectRepository.findById(1L).ifPresent(project -> {
				if (recommendationRepository.count() == 0) {
					Recommendation rec1 = new Recommendation();
					rec1.setTriggerEvent("Dependency Vulnerability Detected (Critical)");
					rec1.setSuggestedAction("Halt build pipeline and force patch to version 2.4.1");
					rec1.setExpectedImpactPercentage(85);
					rec1.setProject(project);

					Recommendation rec2 = new Recommendation();
					rec2.setTriggerEvent("Budget Burn Rate > 15% Expected");
					rec2.setSuggestedAction("Freeze non-essential cloud resource provisioning");
					rec2.setExpectedImpactPercentage(40);
					rec2.setProject(project);

					recommendationRepository.saveAll(List.of(rec1, rec2));
					System.out.println(">>> AI RECOMMENDATIONS SEEDED <<<");
				}
			});
		};
	}
}