package com.disaster.response.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an assignment of a ResponseTeam to an Incident.
 * Created by TaskAssignmentService using the Strategy pattern.
 */
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_team_id", nullable = false)
    private ResponseTeam assignedTeam;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.ASSIGNED;

    private String notes;

    /** Which strategy was used to assign this task (for audit trail). */
    private String assignmentStrategy;

    private Integer priority;

    private LocalDateTime assignedAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime completedAt;

    // ===== Constructors =====
    public Task() {}

    public Task(Incident incident, ResponseTeam assignedTeam, String notes,
                String assignmentStrategy, Integer priority) {
        this.incident = incident;
        this.assignedTeam = assignedTeam;
        this.notes = notes;
        this.assignmentStrategy = assignmentStrategy;
        this.priority = priority;
        this.status = TaskStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ===== LoD-safe accessors (no chaining on external objects) =====
    public String getIncidentTitle() {
        return incident != null ? incident.getTitle() : "Unknown";
    }

    public String getAssignedTeamName() {
        return assignedTeam != null ? assignedTeam.getName() : "Unassigned";
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }

    public ResponseTeam getAssignedTeam() { return assignedTeam; }
    public void setAssignedTeam(ResponseTeam assignedTeam) { this.assignedTeam = assignedTeam; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getAssignmentStrategy() { return assignmentStrategy; }
    public void setAssignmentStrategy(String assignmentStrategy) { this.assignmentStrategy = assignmentStrategy; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
