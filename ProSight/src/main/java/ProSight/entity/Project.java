package ProSight.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Double budget;

    private String status;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private java.util.List<Task> tasks;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private java.util.List<Alert> alerts;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private java.util.List<Recommendation> recommendations;
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private java.util.List<Milestone> milestones;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private java.util.List<Risk> risks;
    public Project() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}