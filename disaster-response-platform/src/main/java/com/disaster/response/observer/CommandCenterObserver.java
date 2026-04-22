package com.disaster.response.observer;

import com.disaster.response.model.CommandHistory;
import com.disaster.response.model.Incident;
import com.disaster.response.repository.CommandHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN — Concrete Observer
 *
 * Logs every incident event to the CommandHistory table so the Command Center
 * can display a real-time audit trail.
 * SRP: Only responsible for persisting incident events as command history records.
 */
@Component
public class CommandCenterObserver implements IncidentObserver {

    private static final Logger log = LoggerFactory.getLogger(CommandCenterObserver.class);

    private final CommandHistoryRepository commandHistoryRepository;

    public CommandCenterObserver(CommandHistoryRepository commandHistoryRepository) {
        this.commandHistoryRepository = commandHistoryRepository;
    }

    @Override
    public void onIncidentEvent(Incident incident, String eventType) {
        String description = buildDescription(incident, eventType);
        CommandHistory record = new CommandHistory(
                "INCIDENT_EVENT",
                description,
                "System",
                "Incident",
                incident.getId()
        );
        commandHistoryRepository.save(record);
        log.info("[CommandCenter] {}", description);
    }

    private String buildDescription(Incident incident, String eventType) {
        return switch (eventType) {
            case "CREATED"        -> "New incident reported: '" + incident.getTitle() + "' [" + incident.getSeverity() + "] at " + incident.getLocation();
            case "STATUS_UPDATED" -> "Incident '" + incident.getTitle() + "' status changed to " + incident.getStatus().getDisplayName();
            case "TEAM_ASSIGNED"  -> "Team assigned to incident '" + incident.getTitle() + "'";
            case "RESOLVED"       -> "Incident '" + incident.getTitle() + "' has been RESOLVED";
            default               -> "Incident '" + incident.getTitle() + "' event: " + eventType;
        };
    }

    @Override
    public String getObserverName() {
        return "CommandCenterObserver";
    }
}
