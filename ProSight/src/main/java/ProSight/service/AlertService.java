package ProSight.service;

import ProSight.entity.Alert;
import ProSight.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert createAlert(Alert alert) {
        return alertRepository.save(alert);
    }

    public List<Alert> getProjectAlerts(Long projectId) {
        return alertRepository.findByProjectId(projectId);
    }
}