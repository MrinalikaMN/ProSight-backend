package ProSight.controller;

import ProSight.entity.Recommendation;
import ProSight.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/project/{projectId}")
    public List<Recommendation> getRecommendationsForProject(@PathVariable("projectId") Long projectId) {
        return recommendationService.getProjectRecommendations(projectId);
    }
}