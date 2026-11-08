package ProSight.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Double cost;
    private String priority;
    private String status;
    private String deadline;
    private String role;

    // Username of the person doing the task (null = unassigned)
    private String assignee;

    // Used for forecasting: when the task was created and when it was completed.
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if ("Completed".equalsIgnoreCase(status) && completedAt == null) completedAt = LocalDateTime.now();
    }

    /** Changes the status and keeps completedAt in step with it. */
    public void changeStatus(String newStatus) {
        boolean wasDone = "Completed".equalsIgnoreCase(status);
        boolean isDone = "Completed".equalsIgnoreCase(newStatus);
        if (isDone && !wasDone) completedAt = LocalDateTime.now();
        if (!isDone) completedAt = null;
        status = newStatus;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}
