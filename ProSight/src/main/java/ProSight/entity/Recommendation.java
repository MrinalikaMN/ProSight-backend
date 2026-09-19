package ProSight.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String triggerEvent;
    private String suggestedAction;
    private int expectedImpactPercentage;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project;

    public Recommendation() {
    }

    // Setters allowing the Java engine to write suggestions
    public void setTriggerEvent(String triggerEvent) { this.triggerEvent = triggerEvent; }
    public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }
    public void setExpectedImpactPercentage(int expectedImpactPercentage) { this.expectedImpactPercentage = expectedImpactPercentage; }
    public void setProject(Project project) { this.project = project; }

    // Getters for Postman
    public String getSuggestedAction() { return suggestedAction; }
    public int getExpectedImpactPercentage() { return expectedImpactPercentage; }
    public Long getId() { return id; }
    public String getTriggerEvent() { return triggerEvent; }
}