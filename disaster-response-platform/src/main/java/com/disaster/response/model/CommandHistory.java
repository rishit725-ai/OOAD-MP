package com.disaster.response.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Audit record for the Command pattern — every executed Command is persisted here.
 * SRP: Only stores command execution history.
 */
@Entity
@Table(name = "command_history")
public class CommandHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String commandType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String executedBy;

    private LocalDateTime executedAt = LocalDateTime.now();

    private Boolean undone = false;

    private String relatedEntityType;
    private Long relatedEntityId;

    // ===== Constructors =====
    public CommandHistory() {}

    public CommandHistory(String commandType, String description, String executedBy,
                          String relatedEntityType, Long relatedEntityId) {
        this.commandType = commandType;
        this.description = description;
        this.executedBy = executedBy;
        this.executedAt = LocalDateTime.now();
        this.undone = false;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
    }

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExecutedBy() { return executedBy; }
    public void setExecutedBy(String executedBy) { this.executedBy = executedBy; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public Boolean getUndone() { return undone; }
    public void setUndone(Boolean undone) { this.undone = undone; }

    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }

    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
}
