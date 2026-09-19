package ProSight.controller;

import ProSight.entity.Milestone;
import ProSight.service.MilestoneService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/milestones")
@CrossOrigin(origins = "*")
public class MilestoneController {
    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @PostMapping
    public Milestone createMilestone(@RequestBody Milestone milestone) {
        return milestoneService.createMilestone(milestone);
    }

    @GetMapping("/project/{projectId}")
    public List<Milestone> getMilestonesForProject(@PathVariable Long projectId) {
        return milestoneService.getProjectMilestones(projectId);
    }
}