package ProSight.risk;

import ProSight.entity.Alert;
import ProSight.entity.Project;
import ProSight.entity.Recommendation;
import ProSight.service.AlertService;
import ProSight.service.RecommendationService;
import org.springframework.stereotype.Service;

@Service
public class RiskAnalysisService {

    private final AlertService alertService;
    private final RecommendationService recommendationService; // NEW

    // Injecting both services into the engine
    public RiskAnalysisService(AlertService alertService, RecommendationService recommendationService) {
        this.alertService = alertService;
        this.recommendationService = recommendationService;
    }

    public double calculateScheduleRisk(double expectedProgress, double actualProgress) {
        double deviation = expectedProgress - actualProgress;
        if (deviation <= 0) return 0.0;
        if (deviation <= 5.0) return 25.0;
        if (deviation <= 15.0) return 50.0;
        return 100.0;
    }

    public double calculateProjectHealth(double schedule, double task, double team, double dependency, double quality, double budget) {
        double health = (0.30 * schedule) + (0.20 * task) + (0.15 * team) + (0.15 * dependency) + (0.10 * quality) + (0.10 * budget);
        return Math.round(health * 10.0) / 10.0;
    }

    public String determineRiskLevel(double healthScore) {
        if (healthScore >= 80) return "HEALTHY (Low Risk)";
        if (healthScore >= 60) return "MEDIUM RISK";
        return "CRITICAL RISK";
    }

    // UPDATED: The engine now generates both Alerts AND Recommendations
    public String checkScheduleAndWarn(Project project, double expected, double actual) {
        double deviation = expected - actual;

        if (deviation > 10.0) {
            // 1. Generate the Alert
            Alert alert = new Alert();
            alert.setSeverity("HIGH");
            alert.setMessage("Schedule deterioration detected: Project delayed by " + deviation + "%");
            alert.setStatus("UNRESOLVED");
            alert.setProject(project);
            alertService.createAlert(alert);

            // 2. Generate the Intelligent Recommendation
            Recommendation rec = new Recommendation();
            rec.setTriggerEvent("Schedule delay of " + deviation + "%");

            // Dynamic logic: Suggest different actions based on severity
            if (deviation > 20.0) {
                rec.setSuggestedAction("CRITICAL: Halt new feature development and reallocate 2 engineers to core tasks.");
                rec.setExpectedImpactPercentage((int) deviation - 5);
            } else {
                rec.setSuggestedAction("WARNING: Extend immediate milestone by 3 days or reallocate 1 developer.");
                rec.setExpectedImpactPercentage((int) deviation);
            }

            rec.setProject(project);
            recommendationService.createRecommendation(rec); // Save to database

            return "WARNING & RECOMMENDATION GENERATED: " + alert.getMessage();
        }
        return "Project is on track. No alerts generated.";
    }
}