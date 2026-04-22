package com.disaster.response.observer;

import com.disaster.response.model.Incident;

/**
 * OBSERVER PATTERN — Behavioral Pattern
 *
 * All observers that wish to be notified of incident events implement this interface.
 * SRP: Each observer has a single reason to change (its own notification logic).
 * OCP: New observer types can be added without modifying the publisher or existing observers.
 */
public interface IncidentObserver {

    /**
     * Called when an incident changes state.
     *
     * @param incident  the incident that changed
     * @param eventType describes the type of change (e.g. "CREATED", "STATUS_UPDATED", "TEAM_ASSIGNED")
     */
    void onIncidentEvent(Incident incident, String eventType);

    /**
     * Unique name of this observer (used for logging and registration).
     */
    String getObserverName();
}
