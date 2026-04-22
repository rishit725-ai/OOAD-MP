package com.disaster.response.repository;

import com.disaster.response.model.Incident;
import com.disaster.response.model.IncidentStatus;
import com.disaster.response.model.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * SRP: TeamRepository handles only team data persistence.
 * Spring Data JPA eliminates boilerplate — DRY principle in action.
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByStatusOrderByReportedAtDesc(IncidentStatus status);

    List<Incident> findBySeverityOrderByReportedAtDesc(Severity severity);

    List<Incident> findAllByOrderByReportedAtDesc();

    @Query("SELECT i FROM Incident i WHERE i.status IN ('REPORTED','ASSIGNED','IN_PROGRESS') ORDER BY i.severity DESC, i.reportedAt DESC")
    List<Incident> findActiveIncidents();

    long countByStatus(IncidentStatus status);

    long countBySeverity(Severity severity);
}
