package ProSight.controller;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final ProjectRepository projectRepository;

    public AnalyticsController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping("/projects")
    public ResponseEntity<Map<String, Object>> getProjectAnalytics() {
        List<Project> projects = projectRepository.findAll();

        int planning = 0;
        int inProgress = 0;
        int completed = 0;
        double totalAllocated = 0.0;
        double totalSpent = 0.0;

        for (Project p : projects) {
            String status = p.getStatus() != null ? p.getStatus().toLowerCase() : "";
            if (status.contains("plan") || status.contains("todo")) planning++;
            else if (status.contains("progress")) inProgress++;
            else if (status.contains("complet")) completed++;

            // Sum Financials
            if (p.getBudget() != null) {
                totalAllocated += p.getBudget();
            }

            if (p.getTasks() != null) {
                double projectSpent = p.getTasks().stream()
                        .mapToDouble(t -> t.getCost() != null ? t.getCost() : 0.0)
                        .sum();
                totalSpent += projectSpent;
            }
        }

        double costSavings = totalAllocated - totalSpent;

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("planningCount", planning);
        metrics.put("inProgressCount", inProgress);
        metrics.put("completedCount", completed);
        metrics.put("totalAllocated", totalAllocated);
        metrics.put("totalSpent", totalSpent);
        metrics.put("costSavings", costSavings);

        return ResponseEntity.ok(metrics);
    }
}