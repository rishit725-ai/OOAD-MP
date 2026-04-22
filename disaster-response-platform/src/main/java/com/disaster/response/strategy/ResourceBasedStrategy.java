package com.disaster.response.strategy;

import com.disaster.response.model.Incident;
import com.disaster.response.model.ResponseTeam;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * STRATEGY PATTERN — Resource-Based Assignment
 *
 * Selects the team whose current load ratio is lowest (most under-utilised),
 * ensuring even distribution of work across all available teams.
 *
 * OCP: Added independently — no changes to existing strategies required.
 */
@Component("resourceBasedStrategy")
public class ResourceBasedStrategy implements TaskAssignmentStrategy {

    @Override
    public ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams) {
        if (availableTeams == null || availableTeams.isEmpty()) return null;

        return availableTeams.stream()
                .filter(ResponseTeam::isAvailable)
                .min(Comparator.comparingDouble(this::getLoadRatio))
                .orElse(null);
    }

    /** Load ratio = currentLoad / capacity (lower = more available resources). */
    private double getLoadRatio(ResponseTeam team) {
        if (team.getCapacity() == null || team.getCapacity() == 0) return Double.MAX_VALUE;
        return (double) team.getCurrentLoad() / team.getCapacity();
    }

    @Override
    public String getStrategyName() {
        return "Resource-Based Strategy";
    }
}
