package ProSight.controller;

import ProSight.entity.Alert;
import ProSight.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/project/{projectId}")
    public List<Alert> getAlertsForProject(@PathVariable Long projectId) {
        return alertService.getProjectAlerts(projectId);
    }
}