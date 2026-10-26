package ProSight.controller;

import ProSight.entity.Task;
import ProSight.repository.ProjectRepository;
import ProSight.repository.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskController(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
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
            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Task taskDetails) {
        return taskRepository.findById(taskId).map(task -> {
            if (taskDetails.getTitle() != null) task.setTitle(taskDetails.getTitle());
            if (taskDetails.getDescription() != null) task.setDescription(taskDetails.getDescription());
            if (taskDetails.getStatus() != null) task.setStatus(taskDetails.getStatus());
            if (taskDetails.getDeadline() != null) task.setDeadline(taskDetails.getDeadline());
            if (taskDetails.getPriority() != null) task.setPriority(taskDetails.getPriority());
            if (taskDetails.getRole() != null) task.setRole(taskDetails.getRole());
            if (taskDetails.getCost() != null) task.setCost(taskDetails.getCost());
            return ResponseEntity.ok(taskRepository.save(task));
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