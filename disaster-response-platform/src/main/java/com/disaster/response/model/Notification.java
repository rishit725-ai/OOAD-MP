package com.disaster.response.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification record created by the Observer pattern when an incident is updated.
 * SRP: Notification only stores the message — NotificationService manages creation.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String type;   // INFO, WARNING, ALERT, SUCCESS

    private LocalDateTime createdAt = LocalDateTime.now();

    private Boolean isRead = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id")
    private Incident incident;

    // ===== Constructors =====
    public Notification() {}

    public Notification(String title, String message, String type, Incident incident) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.incident = incident;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Incident getIncident() { return incident; }
    public void setIncident(Incident incident) { this.incident = incident; }

    public String getBootstrapAlertClass() {
        return switch (type != null ? type : "INFO") {
            case "WARNING" -> "warning";
            case "ALERT"   -> "danger";
            case "SUCCESS" -> "success";
            default        -> "info";
        };
    }
}
