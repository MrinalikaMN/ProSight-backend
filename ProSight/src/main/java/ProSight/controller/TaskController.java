package ProSight.controller;

import ProSight.entity.Project;
import ProSight.entity.Task;
import ProSight.repository.TaskRepository;
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

    public TaskController(TaskRepository taskRepository, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }

    @GetMapping("/{projectId}/tasks")
    public List<Task> getProjectTasks(@PathVariable Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Task> addTaskToProject(@PathVariable Long projectId, @RequestBody Task task) {
        Project project = projectService.getProjectById(projectId);
        if (project == null) {
            return ResponseEntity.notFound().build();
        }

        // Link the task to the specific project before saving
        task.setProject(project);

        // Give it a default status if one wasn't provided
        if (task.getStatus() == null) {
            task.setStatus("To Do");
        }

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.ok(savedTask);
    }
}