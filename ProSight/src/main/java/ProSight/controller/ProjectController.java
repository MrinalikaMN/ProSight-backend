package ProSight.controller;

import ProSight.entity.Project;
import ProSight.repository.AlertRepository;
import ProSight.repository.MilestoneRepository;
import ProSight.repository.ProjectRepository;
import ProSight.repository.RecommendationRepository;
import ProSight.repository.RiskRepository;
import ProSight.repository.WarningRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final RiskRepository riskRepository;
    private final RecommendationRepository recommendationRepository;
    private final AlertRepository alertRepository;
    private final MilestoneRepository milestoneRepository;
    private final WarningRepository warningRepository;

    public ProjectController(ProjectRepository projectRepository,
                             RiskRepository riskRepository,
                             RecommendationRepository recommendationRepository,
                             AlertRepository alertRepository,
                             MilestoneRepository milestoneRepository,
                             WarningRepository warningRepository) {
        this.projectRepository = projectRepository;
        this.riskRepository = riskRepository;
        this.recommendationRepository = recommendationRepository;
        this.alertRepository = alertRepository;
        this.milestoneRepository = milestoneRepository;
        this.warningRepository = warningRepository;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        if (project.getBudget() == null) {
            project.setBudget(0.0);
        }
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("planning");
        }
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        return projectRepository.findById(id)
                .map(project -> {
                    if (projectDetails.getName() != null) {
                        project.setName(projectDetails.getName());
                    }
                    if (projectDetails.getStatus() != null) {
                        project.setStatus(projectDetails.getStatus());
                    }
                    if (projectDetails.getBudget() != null) {
                        project.setBudget(projectDetails.getBudget());
                    }
                    Project updatedProject = projectRepository.save(project);
                    return ResponseEntity.ok(updatedProject);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        if (!projectRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        // Remove everything that points at this project first, otherwise the database refuses
        // to delete it. Tasks are removed automatically with the project (cascade in Project.java).
        // @Transactional: either everything is deleted, or nothing is.
        recommendationRepository.deleteAll(recommendationRepository.findByProjectId(id));
        riskRepository.deleteAll(riskRepository.findByProjectId(id));
        alertRepository.deleteAll(alertRepository.findByProjectId(id));
        milestoneRepository.deleteAll(milestoneRepository.findByProjectId(id));
        warningRepository.deleteAll(warningRepository.findByProjectId(id));
        projectRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}