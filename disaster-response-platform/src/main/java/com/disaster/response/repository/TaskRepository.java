package com.disaster.response.repository;

import com.disaster.response.model.Task;
import com.disaster.response.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByIncidentIdOrderByAssignedAtDesc(Long incidentId);

    List<Task> findByAssignedTeamIdOrderByAssignedAtDesc(Long teamId);

    List<Task> findAllByOrderByAssignedAtDesc();

    long countByStatus(TaskStatus status);
}
