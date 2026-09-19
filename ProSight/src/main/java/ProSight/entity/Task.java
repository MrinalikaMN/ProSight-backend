package ProSight.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String assignedTo;
    private String priority;
    private String status;
    private Double completionPercentage;

    // This creates the foreign key linking the Task to a specific Project
    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Task() {
    }

    // Getters and Setters can be generated later
}