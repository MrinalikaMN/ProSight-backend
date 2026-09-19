package ProSight.service;

import ProSight.entity.Milestone;
import ProSight.repository.MilestoneRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;

    public MilestoneService(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    public Milestone createMilestone(Milestone milestone) {
        return milestoneRepository.save(milestone);
    }

    public List<Milestone> getProjectMilestones(Long projectId) {
        return milestoneRepository.findByProjectId(projectId);
    }
}