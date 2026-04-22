package com.disaster.response.observer;

import com.disaster.response.model.Incident;
import com.disaster.response.model.Notification;
import com.disaster.response.model.Severity;
import com.disaster.response.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * OBSERVER PATTERN — Concrete Observer
 *
 * Creates user-facing Notification records when incidents change.
 * SRP: Only responsible for creating notifications — not for logging or assignment.
 */
@Component
public class NotificationObserver implements IncidentObserver {

    private static final Logger log = LoggerFactory.getLogger(NotificationObserver.class);

    private final NotificationRepository notificationRepository;

    public NotificationObserver(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void onIncidentEvent(Incident incident, String eventType) {
        String notifType = deriveNotificationType(incident, eventType);
        String title = buildTitle(eventType);
        String message = buildMessage(incident, eventType);

        Notification notification = new Notification(title, message, notifType, incident);
        notificationRepository.save(notification);
        log.info("[NotificationObserver] Created notification: {}", title);
    }

    private String deriveNotificationType(Incident incident, String eventType) {
        if ("CREATED".equals(eventType) && incident.getSeverity() == Severity.CRITICAL) return "ALERT";
        if ("RESOLVED".equals(eventType)) return "SUCCESS";
        if ("STATUS_UPDATED".equals(eventType)) return "INFO";
        return "WARNING";
    }

    private String buildTitle(String eventType) {
        return switch (eventType) {
            case "CREATED"        -> "New Incident Reported";
            case "STATUS_UPDATED" -> "Incident Status Updated";
            case "TEAM_ASSIGNED"  -> "Team Assigned to Incident";
            case "RESOLVED"       -> "Incident Resolved";
            default               -> "Incident Update";
        };
    }

    private String buildMessage(Incident incident, String eventType) {
        return switch (eventType) {
            case "CREATED"        -> incident.getSeverity().getDisplayName() + " severity incident at " + incident.getLocation() + ": " + incident.getTitle();
            case "STATUS_UPDATED" -> "'" + incident.getTitle() + "' is now " + incident.getStatus().getDisplayName();
            case "TEAM_ASSIGNED"  -> "A response team has been dispatched to '" + incident.getTitle() + "'";
            case "RESOLVED"       -> "Incident '" + incident.getTitle() + "' at " + incident.getLocation() + " has been successfully resolved";
            default               -> "Update for incident: " + incident.getTitle();
        };
    }

    @Override
    public String getObserverName() {
        return "NotificationObserver";
    }
}
