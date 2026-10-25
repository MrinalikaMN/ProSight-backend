package ProSight.repository;

import ProSight.model.Warning;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarningRepository extends JpaRepository<Warning, Long> {
    List<Warning> findByProjectId(Long projectId);
}