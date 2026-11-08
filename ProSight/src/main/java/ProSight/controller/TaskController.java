package ProSight.controller;

import ProSight.entity.Task;
import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import ProSight.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notificationService;

    public TaskController(TaskRepository taskRepository, ProjectRepository projectRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.notificationService = notificationService;
    }

    private static String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? null : auth.getName();
    }

    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskRepository.findByProjectId(projectId));
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<?> addTaskToProject(@PathVariable Long projectId, @RequestBody Task task) {
        return projectRepository.findById(projectId).map(project -> {
            task.setProject(project);
            if (task.getStatus() == null || task.getStatus().isEmpty()) {
                task.setStatus("To Do");
            }
            // createdAt / completedAt are set by the server, never trusted from the browser
            task.setCreatedAt(null);
            task.setCompletedAt(null);
            if (task.getAssignee() != null && task.getAssignee().isBlank()) task.setAssignee(null);
            Task saved = taskRepository.save(task);
            notificationService.notifyAssigned(saved, currentUsername());
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Task taskDetails) {
        return taskRepository.findById(taskId).map(task -> {
            String previousAssignee = task.getAssignee();
            if (taskDetails.getTitle() != null) task.setTitle(taskDetails.getTitle());
            if (taskDetails.getDescription() != null) task.setDescription(taskDetails.getDescription());
            if (taskDetails.getStatus() != null) task.changeStatus(taskDetails.getStatus());
            if (taskDetails.getDeadline() != null) task.setDeadline(taskDetails.getDeadline());
            if (taskDetails.getPriority() != null) task.setPriority(taskDetails.getPriority());
            if (taskDetails.getRole() != null) task.setRole(taskDetails.getRole());
            if (taskDetails.getCost() != null) task.setCost(taskDetails.getCost());
            // "" means "unassign"; null means "not sent, leave as is"
            if (taskDetails.getAssignee() != null) {
                task.setAssignee(taskDetails.getAssignee().isBlank() ? null : taskDetails.getAssignee());
            }
            Task saved = taskRepository.save(task);
            // Email the new assignee only when the task changes hands
            if (saved.getAssignee() != null && !saved.getAssignee().equals(previousAssignee)) {
                notificationService.notifyAssigned(saved, currentUsername());
            }
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}