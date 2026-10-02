package ProSight.repository;

import ProSight.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Custom query to find all tasks linked to a specific project
    List<Task> findByProjectId(Long projectId);
}