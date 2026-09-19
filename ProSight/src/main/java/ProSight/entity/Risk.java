package ProSight.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "risks")
public class Risk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String likelihood;
    private String impact;
    private String status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Project project;

    public Risk() {
    }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLikelihood(String likelihood) { this.likelihood = likelihood; }
    public void setImpact(String impact) { this.impact = impact; }
    public void setStatus(String status) { this.status = status; }
    public void setProject(Project project) { this.project = project; }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLikelihood() { return likelihood; }
    public String getImpact() { return impact; }
    public String getStatus() { return status; }
    public Project getProject() { return project; }
}