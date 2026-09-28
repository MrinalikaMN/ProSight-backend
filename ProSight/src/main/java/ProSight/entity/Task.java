package ProSight.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // To track utilization (e.g., "Backend", "Frontend", "QA")
    private String assignedTeam;

    // To feed the Early Warning Algorithm
    private Double expectedDays;
    private Double actualDays;
    private String status = "In Progress"; // "To Do", "In Progress", "Done"

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore // Prevents infinite recursion when fetching projects
    private Project project;

    public Task() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(String assignedTeam) { this.assignedTeam = assignedTeam; }

    public Double getExpectedDays() { return expectedDays; }
    public void setExpectedDays(Double expectedDays) { this.expectedDays = expectedDays; }

    public Double getActualDays() { return actualDays; }
    public void setActualDays(Double actualDays) { this.actualDays = actualDays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }
}