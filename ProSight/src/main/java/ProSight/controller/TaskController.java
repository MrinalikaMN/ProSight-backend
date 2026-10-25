package ProSight.controller;

import ProSight.entity.Project;
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
            if (task.getStatus() == null) {
                task.setStatus("To Do");
            }
            if (task.getPriority() == null) {
                task.setPriority("Medium");
            }
            if (task.getCost() == null) {
                task.setCost(0.0);
            }
            return ResponseEntity.ok(taskRepository.save(task));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody Task taskDetails) {
        return taskRepository.findById(taskId).map(task -> {
            task.setName(taskDetails.getName());
            task.setDescription(taskDetails.getDescription());
            task.setStatus(taskDetails.getStatus());
            task.setDueDate(taskDetails.getDueDate());
            task.setPriority(taskDetails.getPriority());
            task.setAssignee(taskDetails.getAssignee());
            task.setCost(taskDetails.getCost());
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