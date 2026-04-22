package com.disaster.response.strategy;

import com.disaster.response.model.Incident;
import com.disaster.response.model.ResponseTeam;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * STRATEGY PATTERN — Proximity-Based Assignment
 *
 * Selects the available team closest to the incident location using the Haversine formula.
 * ResponseTeam.distanceTo() encapsulates the distance calculation (LoD — no chaining).
 *
 * OCP: This class can be swapped at runtime without modifying TaskAssignmentService.
 */
@Component("proximityBasedStrategy")
public class ProximityBasedStrategy implements TaskAssignmentStrategy {

    @Override
    public ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams) {
        if (availableTeams == null || availableTeams.isEmpty()) return null;
        if (incident.getLatitude() == null || incident.getLongitude() == null) {
            // Fall back to first available team if no location data
            return availableTeams.stream().filter(ResponseTeam::isAvailable).findFirst().orElse(null);
        }

        double incidentLat = incident.getLatitude();
        double incidentLon = incident.getLongitude();

        return availableTeams.stream()
                .filter(ResponseTeam::isAvailable)
                .min(Comparator.comparingDouble(
                        team -> team.distanceTo(incidentLat, incidentLon)))
                .orElse(null);
    }

    @Override
    public String getStrategyName() {
        return "Proximity-Based Strategy";
    }
}
