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

        // Count totals directly from the MySQL database
        long totalProjects = projectRepository.count();
        long totalUsers = userRepository.count();
        long totalTasks = taskRepository.count();

        stats.put("totalProjects", totalProjects);
        stats.put("totalUsers", totalUsers);
        stats.put("totalTasks", totalTasks);

        return ResponseEntity.ok(stats);
    }
}