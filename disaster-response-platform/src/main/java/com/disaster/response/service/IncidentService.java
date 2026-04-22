package com.disaster.response.service;

import com.disaster.response.model.Incident;
import com.disaster.response.model.IncidentStatus;
import com.disaster.response.model.Severity;
import com.disaster.response.observer.IncidentEventPublisher;
import com.disaster.response.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SRP: IncidentService handles all Incident business logic.
 * Does NOT do assignment (TaskAssignmentService) or notifications (NotificationService).
 * LoD: Communicates only with its direct collaborators (repository, publisher).
 */
@Service
@Transactional
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentEventPublisher eventPublisher;

    public IncidentService(IncidentRepository incidentRepository,
                           IncidentEventPublisher eventPublisher) {
        this.incidentRepository = incidentRepository;
        this.eventPublisher = eventPublisher;
    }

    public Incident createIncident(Incident incident) {
        incident.setStatus(IncidentStatus.REPORTED);
        incident.setReportedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());
        Incident saved = incidentRepository.save(incident);
        eventPublisher.publish(saved, "CREATED");
        return saved;
    }

    public Incident updateIncident(Incident incident) {
        incident.setUpdatedAt(LocalDateTime.now());
        return incidentRepository.save(incident);
    }

    public void updateStatus(Long id, IncidentStatus newStatus) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
        incident.setStatus(newStatus);
        incident.setUpdatedAt(LocalDateTime.now());
        if (newStatus == IncidentStatus.RESOLVED || newStatus == IncidentStatus.CLOSED) {
            incident.setResolvedAt(LocalDateTime.now());
        }
        incidentRepository.save(incident);
        String eventType = (newStatus == IncidentStatus.RESOLVED) ? "RESOLVED" : "STATUS_UPDATED";
        eventPublisher.publish(incident, eventType);
    }

    public Optional<Incident> findById(Long id) {
        return incidentRepository.findById(id);
    }

    public List<Incident> findAll() {
        return incidentRepository.findAllByOrderByReportedAtDesc();
    }

    public List<Incident> findActive() {
        return incidentRepository.findActiveIncidents();
    }

    public void deleteById(Long id) {
        incidentRepository.deleteById(id);
    }

    // ===== Dashboard statistics =====
    public long countByStatus(IncidentStatus status) {
        return incidentRepository.countByStatus(status);
    }

    public long countBySeverity(Severity severity) {
        return incidentRepository.countBySeverity(severity);
    }

    public long countTotal() {
        return incidentRepository.count();
    }
}
