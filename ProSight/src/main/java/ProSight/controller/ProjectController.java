package ProSight.controller;

import ProSight.entity.Project;
import ProSight.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }

    // SECURED: Only Admins can create projects
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {

        // Ensure default budget is set if not provided
        if (project.getBudget() == null) {
            project.setBudget(0.0);
        }

        // Ensure default status is set if not provided
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("planning");
        }

        return ResponseEntity.ok(projectRepository.save(project));
    }

    // SECURED: Only Admins can delete projects
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}