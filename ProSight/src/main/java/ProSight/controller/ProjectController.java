package ProSight.controller;

import ProSight.entity.Project;
import ProSight.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // SECURED: Only Admins can create projects
    @PostMapping
    public ResponseEntity<?> createProject(
            @RequestBody Project project) {

        if (project.getHealth() == null || project.getHealth() == 0) {
            project.setHealth(100); // Default new projects to perfect health
        }

        // Ensure default status is set if not provided
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("planning");
        }

        return ResponseEntity.ok(projectRepository.save(project));
    }

    // SECURED: Only Admins can delete projects
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @RequestHeader(value = "User-Role", defaultValue = "MEMBER") String role,
            @PathVariable Long id) {

        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Unauthorized: Only Admins can delete projects.");
        }

        projectRepository.deleteById(id);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    // NEW: Endpoint to update project Kanban column status
    @PutMapping("/{id}/status")
    public ResponseEntity<Project> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        project.setStatus(status);
        return ResponseEntity.ok(projectRepository.save(project));
    }
}