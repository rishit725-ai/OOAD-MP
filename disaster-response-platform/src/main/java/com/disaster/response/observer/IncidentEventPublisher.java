package com.disaster.response.observer;

import com.disaster.response.model.Incident;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * OBSERVER PATTERN — Subject / Publisher
 *
 * Maintains the list of observers and notifies them when an incident changes.
 * Law of Demeter: Observers are called directly — no a.b().c() chains.
 * SRP: Only responsible for managing observers and dispatching events.
 */
@Component
public class IncidentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(IncidentEventPublisher.class);

    private final List<IncidentObserver> observers = new ArrayList<>();

    /** Register an observer to receive incident events. */
    public void subscribe(IncidentObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            log.info("Observer registered: {}", observer.getObserverName());
        }
    }

    /** Remove an observer. */
    public void unsubscribe(IncidentObserver observer) {
        observers.remove(observer);
        log.info("Observer removed: {}", observer.getObserverName());
    }

    /** Notify all registered observers of an incident event. */
    public void publish(Incident incident, String eventType) {
        log.info("Publishing event '{}' for incident: {}", eventType, incident.getTitle());
        for (IncidentObserver observer : observers) {
            try {
                observer.onIncidentEvent(incident, eventType);
            } catch (Exception e) {
                log.error("Observer {} failed to handle event: {}", observer.getObserverName(), e.getMessage());
            }
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}
