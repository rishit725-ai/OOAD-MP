package com.disaster.response.strategy;

import com.disaster.response.model.Incident;
import com.disaster.response.model.ResponseTeam;

import java.util.List;

/**
 * STRATEGY PATTERN — Behavioral Pattern
 * OCP: New assignment strategies can be added without modifying existing code.
 *
 * Interface that all concrete assignment strategies implement.
 * TaskAssignmentService selects the appropriate strategy at runtime.
 */
public interface TaskAssignmentStrategy {

    /**
     * Selects the best available team for the given incident.
     *
     * @param incident       the incident requiring a response team
     * @param availableTeams list of teams currently available for assignment
     * @return the selected ResponseTeam, or null if none suitable
     */
    ResponseTeam assignTeam(Incident incident, List<ResponseTeam> availableTeams);

    /**
     * Human-readable name displayed in the UI and audit log.
     */
    String getStrategyName();
}
