package ProSight.controller;

import ProSight.entity.Project;
import ProSight.service.ProjectService;
import ProSight.risk.RiskAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    private final RiskAnalysisService riskAnalysisService;

    public ProjectController(ProjectService projectService, RiskAnalysisService riskAnalysisService) {
        this.projectService = projectService;
        this.riskAnalysisService = riskAnalysisService;
    }

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project existingProject = projectService.getProjectById(id);

        if (existingProject == null) {
            return ResponseEntity.notFound().build();
        }

        existingProject.setName(projectDetails.getName());
        existingProject.setBudget(projectDetails.getBudget());
        existingProject.setHealth(projectDetails.getHealth());

        Project updatedProject = projectService.createProject(existingProject);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping("/analyze-risk")
    public double testRiskEngine(@RequestParam double expected, @RequestParam double actual) {
        return riskAnalysisService.calculateScheduleRisk(expected, actual);
    }

    @GetMapping("/health-score")
    public String getProjectHealth(
            @RequestParam double schedule,
            @RequestParam double task,
            @RequestParam double team,
            @RequestParam double dependency,
            @RequestParam double quality,
            @RequestParam double budget) {

        double score = riskAnalysisService.calculateProjectHealth(schedule, task, team, dependency, quality, budget);
        String riskLevel = riskAnalysisService.determineRiskLevel(score);

        return "Project Health Score: " + score + " / 100 | Status: " + riskLevel;
    }

    @PostMapping("/{projectId}/check-warning")
    public String checkProjectWarning(
            @PathVariable Long projectId,
            @RequestParam double expected,
            @RequestParam double actual) {

        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return "Error: Project not found in database.";
        }

        return riskAnalysisService.checkScheduleAndWarn(project, expected, actual);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }
}