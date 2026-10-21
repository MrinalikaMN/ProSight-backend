package ProSight.repository;

import ProSight.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);

    // NEW: Count tasks by their specific status for the dashboard charts
    long countByStatus(String status);
}