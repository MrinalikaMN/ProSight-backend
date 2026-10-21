package ProSight.controller;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warnings")
@CrossOrigin(origins = "*")
public class WarningController {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public WarningController(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAutomatedWarnings() {
        List<Project> projects = projectRepository.findAll();
        List<Map<String, Object>> warnings = new ArrayList<>();

        for (Project p : projects) {
            List<String> issues = new ArrayList<>();
            String severity = "LOW";

            // Rule 1: Health Diagnostics
            if (p.getHealth() <= 40) {
                issues.add("Critical Health (" + p.getHealth() + "/100). Project is severely underperforming.");
                severity = "CRITICAL";
            } else if (p.getHealth() <= 60) {
                issues.add("Low Health (" + p.getHealth() + "/100). Close monitoring recommended.");
                if (!"CRITICAL".equals(severity)) severity = "HIGH";
            }

            // Rule 2: Capital Risk
            if (p.getBudget() != null && p.getBudget() >= 50000 && p.getHealth() < 70) {
                issues.add("High Capital at Risk: Large budget ($" + p.getBudget() + ") allocated to a destabilizing project.");
                severity = "CRITICAL";
            }

            // Rule 3: Progress Tracking
            List<Task> tasks = taskRepository.findByProjectId(p.getId());
            if (tasks != null && !tasks.isEmpty()) {
                long completed = tasks.stream().filter(t -> "Completed".equals(t.getStatus())).count();
                if (completed == 0 && tasks.size() >= 3) {
                    issues.add("Stalled Progress: " + tasks.size() + " tasks logged, but 0 completed.");
                    if ("LOW".equals(severity)) severity = "MEDIUM";
                }
            } else {
                issues.add("No tasks assigned. Project may be improperly planned or abandoned.");
                if ("LOW".equals(severity)) severity = "MEDIUM";
            }

            // If any rules were triggered, package the warning
            if (!issues.isEmpty()) {
                Map<String, Object> warningMap = new HashMap<>();
                warningMap.put("projectId", p.getId());
                warningMap.put("projectName", p.getName());
                warningMap.put("issues", issues);
                warningMap.put("severity", severity);
                warnings.add(warningMap);
            }
        }

        // Sort so CRITICAL warnings appear at the very top
        warnings.sort((w1, w2) -> {
            Map<String, Integer> severityRank = Map.of("CRITICAL", 3, "HIGH", 2, "MEDIUM", 1);
            return severityRank.get(w2.get("severity")).compareTo(severityRank.get(w1.get("severity")));
        });

        return ResponseEntity.ok(warnings);
    }
}