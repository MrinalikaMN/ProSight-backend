package ProSight.controller;

import ProSight.entity.Milestone;
import ProSight.repository.MilestoneRepository;
import ProSight.service.MilestoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;
    private final MilestoneRepository milestoneRepository;

    public MilestoneController(MilestoneService milestoneService, MilestoneRepository milestoneRepository) {
        this.milestoneService = milestoneService;
        this.milestoneRepository = milestoneRepository;
    }

    // Body: { "name": "Beta release", "dueDate": "2026-11-20", "completed": false, "project": { "id": 1 } }
    @PostMapping
    public Milestone createMilestone(@RequestBody Milestone milestone) {
        return milestoneService.createMilestone(milestone);
    }

    @GetMapping("/project/{projectId}")
    public List<Milestone> getMilestonesForProject(@PathVariable Long projectId) {
        return milestoneService.getProjectMilestones(projectId);
    }

    // Only the fields that are sent are changed, e.g. { "completed": true }
    @PutMapping("/{id}")
    public ResponseEntity<Milestone> updateMilestone(@PathVariable Long id, @RequestBody Map<String, Object> changes) {
        return milestoneRepository.findById(id).map(m -> {
            if (changes.get("name") instanceof String name && !name.isBlank()) m.setName(name);
            if (changes.get("dueDate") instanceof String due && !due.isBlank()) m.setDueDate(LocalDate.parse(due));
            if (changes.get("completed") instanceof Boolean done) m.setCompleted(done);
            return ResponseEntity.ok(milestoneRepository.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
        if (!milestoneRepository.existsById(id)) return ResponseEntity.notFound().build();
        milestoneRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
