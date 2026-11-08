package ProSight.component;

import ProSight.entity.Project;
import ProSight.model.Warning;
import ProSight.repository.ProjectRepository;
import ProSight.repository.WarningRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adds a few example team warnings so the Warnings page isn't empty in a fresh database.
 * Runs once the app has fully started (after DatabaseSeeder has created the sample projects),
 * and only if there are no warnings at all yet, so it never creates duplicates.
 */
@Component
public class WarningSeeder {

    private final WarningRepository warningRepository;
    private final ProjectRepository projectRepository;

    public WarningSeeder(WarningRepository warningRepository, ProjectRepository projectRepository) {
        this.warningRepository = warningRepository;
        this.projectRepository = projectRepository;
    }

    // Each row: { project name, warning message, severity }
    private static final List<String[]> SAMPLES = List.<String[]>of(
            new String[] { "E-Commerce Platform Redesign", "Payment provider hasn't confirmed sandbox access for the security audit", "High" },
            new String[] { "E-Commerce Platform Redesign", "Only one developer understands the legacy CSS build", "Medium" },
            new String[] { "Mobile Banking App Redesign", "App store review of biometric login could take over two weeks", "High" },
            new String[] { "Mobile Banking App Redesign", "Push notification wording still needs legal sign-off", "Low" },
            new String[] { "Cloud DevOps Automation Platform", "Cluster costs may exceed budget once autoscaling is switched on", "Medium" }
    );

    @EventListener(ApplicationReadyEvent.class)
    public void seedWarnings() {
        if (warningRepository.count() > 0) {
            return;
        }
        int added = 0;
        for (Project project : projectRepository.findAll()) {
            for (String[] row : SAMPLES) {
                if (row[0].equals(project.getName())) {
                    warningRepository.save(new Warning(row[1], row[2], project.getId()));
                    added++;
                }
            }
        }
        System.out.println(">>> WarningSeeder added " + added + " sample warnings. <<<");
    }
}