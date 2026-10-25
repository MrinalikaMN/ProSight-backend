package ProSight.controller;

import ProSight.model.Warning;
import ProSight.repository.WarningRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/warnings")
public class WarningController {

    private final WarningRepository warningRepository;

    public WarningController(WarningRepository warningRepository) {
        this.warningRepository = warningRepository;
    }

    @GetMapping
    public ResponseEntity<List<Warning>> getWarningsByProject(@PathVariable Long projectId) {
        List<Warning> warnings = warningRepository.findByProjectId(projectId);
        return ResponseEntity.ok(warnings);
    }

    @PostMapping
    public ResponseEntity<Warning> createWarning(@PathVariable Long projectId, @RequestBody Warning warning) {
        warning.setProjectId(projectId);
        Warning savedWarning = warningRepository.save(warning);
        return ResponseEntity.ok(savedWarning);
    }

    @DeleteMapping("/{warningId}")
    public ResponseEntity<?> deleteWarning(@PathVariable Long projectId, @PathVariable Long warningId) {
        warningRepository.deleteById(warningId);
        return ResponseEntity.ok().build();
    }
}