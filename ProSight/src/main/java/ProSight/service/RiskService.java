package ProSight.service;

import ProSight.entity.Risk;
import ProSight.repository.RiskRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RiskService {
    public void deleteRisk(Long id) {
        riskRepository.deleteById(id);
    }
    private final RiskRepository riskRepository;

    public RiskService(RiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    public Risk createRisk(Risk risk) {
        return riskRepository.save(risk);
    }

    public List<Risk> getProjectRisks(Long projectId) {
        return riskRepository.findByProjectId(projectId);
    }
}