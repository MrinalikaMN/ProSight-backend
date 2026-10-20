package ProSight.controller;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.entity.User;
import ProSight.repository.TaskRepository;
import ProSight.repository.UserRepository;
import ProSight.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, ProjectService projectService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{projectId}/tasks")
    public List<Task> getProjectTasks(@PathVariable Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Task> addTaskToProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long assigneeId,
            @RequestBody Task task) {

        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        task.setProject(project);

        if (assigneeId != null) {
            userRepository.findById(assigneeId).ifPresent(task::setAssignee);
        }

        if (task.getStatus() == null) {
            task.setStatus("To Do");
        }

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/tasks/{taskId}/assign/{userId}")
    public ResponseEntity<Task> assignTask(@PathVariable Long taskId, @PathVariable Long userId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (task == null || user == null) {
            return ResponseEntity.notFound().build();
        }

        task.setAssignee(user);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    // Endpoint to update the status of an existing task for the progress bar
    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        Task task = taskRepository.findById(taskId).orElse(null);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        task.setStatus(status);
        return ResponseEntity.ok(taskRepository.save(task));
    }
}