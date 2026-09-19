package ProSight;

import ProSight.entity.Recommendation;
import ProSight.repository.RecommendationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
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
	public CommandLineRunner seedData(ProSight.repository.ProjectRepository projectRepository) {
		return args -> {
			if (projectRepository.findById(1L).isEmpty()) {
				ProSight.entity.Project defaultProject = new ProSight.entity.Project();
				defaultProject.setName("Default Project");
				projectRepository.save(defaultProject);
				System.out.println(">>> DEFAULT PROJECT CREATED <<<");
			}
		};
	}

	@Bean
	public CommandLineRunner seedRecommendations(RecommendationRepository recommendationRepository, ProSight.repository.ProjectRepository projectRepository) {
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