package ProSight.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

@Entity
@Table(name = "milestones")
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dueDate;
    private boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "project_id")
    // Accepted when creating a milestone ({ "project": { "id": 1 } }), never sent back
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Project project;

    public Milestone() {
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
    public void setProject(Project project) { this.project = project; }

    // Getters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isCompleted() { return isCompleted; }
}