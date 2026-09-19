package ProSight.controller;

import ProSight.entity.Recommendation;
import ProSight.entity.Risk;
import ProSight.repository.RecommendationRepository;
import ProSight.repository.RiskRepository;
import ProSight.service.GeminiService;
import ProSight.service.RiskService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/risks")
public class RiskController {

    private final RiskService riskService;
    private final RiskRepository riskRepository;
    private final RecommendationRepository recommendationRepository;
    private final GeminiService geminiService;

    public RiskController(RiskService riskService,
                          RiskRepository riskRepository,
                          RecommendationRepository recommendationRepository,
                          GeminiService geminiService) {
        this.riskService = riskService;
        this.riskRepository = riskRepository;
        this.recommendationRepository = recommendationRepository;
        this.geminiService = geminiService;
    }

    @GetMapping("/project/{projectId}")
    public List<Risk> getRisksForProject(@PathVariable("projectId") Long projectId) {
        return riskService.getProjectRisks(projectId);
    }

    @GetMapping("/test/{impact}")
    public List<String> testRecommendation(@PathVariable("impact") String impact) {
        return Arrays.asList("Test Strategy 1", "Test Strategy 2");
    }

    @PostMapping
    public Risk createRisk(@RequestBody Risk risk) {
        Risk savedRisk = riskRepository.save(risk);

        if (savedRisk.getImpact() != null && savedRisk.getImpact().equalsIgnoreCase("High")) {
            Recommendation autoRec = new Recommendation();

            // REPLACED: .getName() has been changed to .getDescription() to fix the build error.
            // (If your Risk entity uses .getTitle() instead, just swap that here!)
            String riskDetail = savedRisk.getDescription();

            autoRec.setTriggerEvent("High Risk Added: " + riskDetail);
            String dynamicStrategy = geminiService.generateMitigationStrategy(riskDetail);

            autoRec.setSuggestedAction(dynamicStrategy);
            autoRec.setExpectedImpactPercentage(90);
            autoRec.setProject(savedRisk.getProject());

            recommendationRepository.save(autoRec);
            System.out.println(">>> AI GENERATED A UNIQUE MITIGATION STRATEGY <<<");
        }

        return savedRisk;
    }

    @DeleteMapping("/{id}")
    public void deleteRisk(@PathVariable("id") Long id) {
        riskService.deleteRisk(id);
    }
}