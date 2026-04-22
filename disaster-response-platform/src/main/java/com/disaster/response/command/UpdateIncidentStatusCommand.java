package com.disaster.response.command;

import com.disaster.response.model.Incident;
import com.disaster.response.model.IncidentStatus;
import com.disaster.response.repository.IncidentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * COMMAND PATTERN — Concrete Command
 *
 * Updates the status of an Incident.
 * Undo reverts to the previous status.
 *
 * SRP: Only handles incident status transitions.
 */
public class UpdateIncidentStatusCommand implements DisasterCommand {

    private static final Logger log = LoggerFactory.getLogger(UpdateIncidentStatusCommand.class);

    private final Incident incident;
    private final IncidentStatus newStatus;
    private final IncidentStatus previousStatus;  // stored for undo
    private final IncidentRepository incidentRepository;

    public UpdateIncidentStatusCommand(Incident incident, IncidentStatus newStatus,
                                       IncidentRepository incidentRepository) {
        this.incident = incident;
        this.newStatus = newStatus;
        this.previousStatus = incident.getStatus();  // snapshot before change
        this.incidentRepository = incidentRepository;
    }

    @Override
    public void execute() {
        incident.setStatus(newStatus);
        incident.setUpdatedAt(LocalDateTime.now());
        if (newStatus == IncidentStatus.RESOLVED || newStatus == IncidentStatus.CLOSED) {
            incident.setResolvedAt(LocalDateTime.now());
        }
        incidentRepository.save(incident);
        log.info("Status updated: '{}' → {}", incident.getTitle(), newStatus);
    }

    @Override
    public void undo() {
        incident.setStatus(previousStatus);
        incident.setUpdatedAt(LocalDateTime.now());
        incident.setResolvedAt(null);
        incidentRepository.save(incident);
        log.info("Status reverted: '{}' → {}", incident.getTitle(), previousStatus);
    }

    @Override
    public String getDescription() {
        return "Updated incident '" + incident.getTitle() + "' status from "
                + previousStatus.getDisplayName() + " to " + newStatus.getDisplayName();
    }

    @Override
    public String getCommandType() {
        return "UPDATE_STATUS";
    }
}
