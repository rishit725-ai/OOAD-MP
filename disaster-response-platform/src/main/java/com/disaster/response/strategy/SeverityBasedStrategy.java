package com.disaster.response.strategy;

import com.disaster.response.model.Incident;
import com.disaster.response.model.ResponseTeam;
import com.disaster.response.model.Severity;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * STRATEGY PATTERN — Severity-Based Assignment
 *
 * For CRITICAL/HIGH incidents: selects the team with the largest available capacity.
 * For MEDIUM/LOW incidents: selects the team with the smallest sufficient capacity (conserves resources).
 *
 * OCP: Adding a new strategy requires only a new class implementing TaskAssignmentStrategy.
 */
@Component("severityBasedStrategy")
public class SeverityBasedStrategy implements TaskAssignmentStrategy {

    @Override
    public ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams) {
        if (availableTeams == null || availableTeams.isEmpty()) return null;

        Severity severity = incident.getSeverity();

        if (severity == Severity.CRITICAL || severity == Severity.HIGH) {
            // For high-severity: pick the team with the highest available capacity
            return availableTeams.stream()
                    .filter(ResponseTeam::isAvailable)
                    .max(Comparator.comparingInt(ResponseTeam::getAvailableCapacity))
                    .orElse(null);
        } else {
            // For low-severity: pick the team with the smallest capacity still >= 1 (conserve resources)
            return availableTeams.stream()
                    .filter(ResponseTeam::isAvailable)
                    .min(Comparator.comparingInt(ResponseTeam::getAvailableCapacity))
                    .orElse(null);
        }
    }

    @Override
    public String getStrategyName() {
        return "Severity-Based Strategy";
    }
}
