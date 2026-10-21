package ProSight.controller;

import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import ProSight.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public DashboardController(ProjectRepository projectRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProjects", projectRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTasks", taskRepository.count());

        // NEW: Task breakdown for the Recharts Donut Chart
        stats.put("todoTasks", taskRepository.countByStatus("To Do"));
        stats.put("inProgressTasks", taskRepository.countByStatus("In Progress"));
        stats.put("completedTasks", taskRepository.countByStatus("Completed"));

        return ResponseEntity.ok(stats);
    }
}