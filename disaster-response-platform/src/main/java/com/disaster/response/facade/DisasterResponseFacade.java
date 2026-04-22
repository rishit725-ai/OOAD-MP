package com.disaster.response.facade;

import com.disaster.response.command.AssignTeamCommand;
import com.disaster.response.command.CommandInvoker;
import com.disaster.response.command.UpdateIncidentStatusCommand;
import com.disaster.response.model.*;
import com.disaster.response.observer.IncidentEventPublisher;
import com.disaster.response.repository.IncidentRepository;
import com.disaster.response.repository.ResponseTeamRepository;
import com.disaster.response.repository.ShelterRepository;
import com.disaster.response.repository.TaskRepository;
import com.disaster.response.strategy.TaskAssignmentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * FACADE PATTERN — Structural Pattern
 *
 * Provides a simplified, unified interface to the complex subsystems:
 *   - Task Assignment (Strategy pattern)
 *   - Status Updates (Command pattern)
 *   - Notifications (Observer pattern)
 *   - Shelter Management
 *
 * Controllers call this facade instead of each subsystem separately.
 * Law of Demeter: Each field is an immediate collaborator — no chains like a.b().c().
 * SRP: Coordinates subsystems — does not implement their logic.
 */
@Component
public class DisasterResponseFacade {

    private static final Logger log = LoggerFactory.getLogger(DisasterResponseFacade.class);

    private final Map<String, TaskAssignmentStrategy> strategies;
    private final CommandInvoker commandInvoker;
    private final IncidentEventPublisher eventPublisher;
    private final IncidentRepository incidentRepository;
    private final ResponseTeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final ShelterRepository shelterRepository;

    public DisasterResponseFacade(
            Map<String, TaskAssignmentStrategy> strategies,
            CommandInvoker commandInvoker,
            IncidentEventPublisher eventPublisher,
            IncidentRepository incidentRepository,
            ResponseTeamRepository teamRepository,
            TaskRepository taskRepository,
            ShelterRepository shelterRepository) {
        this.strategies = strategies;
        this.commandInvoker = commandInvoker;
        this.eventPublisher = eventPublisher;
        this.incidentRepository = incidentRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.shelterRepository = shelterRepository;
    }

    /**
     * Full emergency response flow:
     * 1. Save incident, 2. Select strategy, 3. Assign team, 4. Notify observers.
     *
     * @param incident       the newly created incident
     * @param strategyKey    bean name of the desired strategy (e.g. "severityBasedStrategy")
     * @return the assigned team, or null if none available
     */
    public ResponseTeam handleNewIncident(Incident incident, String strategyKey) {
        // 1. Persist incident
        Incident saved = incidentRepository.save(incident);

        // 2. Notify: CREATED
        eventPublisher.publish(saved, "CREATED");

        // 3. Select strategy and assign team
        TaskAssignmentStrategy strategy = strategies.getOrDefault(strategyKey,
                strategies.get("severityBasedStrategy"));

        List<ResponseTeam> available = teamRepository.findAvailableTeams();
        ResponseTeam selected = strategy.assignTeam(saved, available);

        if (selected != null) {
            AssignTeamCommand cmd = new AssignTeamCommand(saved, selected,
                    strategy.getStrategyName(), taskRepository, teamRepository);
            commandInvoker.execute(cmd);

            // Update incident status
            UpdateIncidentStatusCommand statusCmd = new UpdateIncidentStatusCommand(
                    saved, IncidentStatus.ASSIGNED, incidentRepository);
            commandInvoker.execute(statusCmd);

            // Notify: TEAM_ASSIGNED
            eventPublisher.publish(saved, "TEAM_ASSIGNED");
            log.info("[Facade] Handled new incident '{}' — assigned to '{}'", saved.getTitle(), selected.getName());
        } else {
            log.warn("[Facade] No available teams for incident '{}'", saved.getTitle());
        }

        return selected;
    }

    /**
     * Update an incident's status and notify all observers.
     */
    public void updateIncidentStatus(Incident incident, IncidentStatus newStatus) {
        UpdateIncidentStatusCommand cmd = new UpdateIncidentStatusCommand(
                incident, newStatus, incidentRepository);
        commandInvoker.execute(cmd);

        String eventType = (newStatus == IncidentStatus.RESOLVED) ? "RESOLVED" : "STATUS_UPDATED";
        eventPublisher.publish(incident, eventType);
        log.info("[Facade] Status updated for '{}' → {}", incident.getTitle(), newStatus);
    }

    /**
     * Manually assign a specific team to an incident using a chosen strategy.
     */
    public Task assignTeamToIncident(Long incidentId, Long teamId, String strategyKey) {
        Optional<Incident> incidentOpt = incidentRepository.findById(incidentId);
        Optional<ResponseTeam> teamOpt = teamRepository.findById(teamId);
        if (incidentOpt.isEmpty() || teamOpt.isEmpty()) return null;

        Incident incident = incidentOpt.get();
        ResponseTeam team = teamOpt.get();

        TaskAssignmentStrategy strategy = strategies.getOrDefault(strategyKey,
                strategies.get("severityBasedStrategy"));

        AssignTeamCommand cmd = new AssignTeamCommand(
                incident, team, strategy.getStrategyName(), taskRepository, teamRepository);
        commandInvoker.execute(cmd);

        eventPublisher.publish(incident, "TEAM_ASSIGNED");
        return cmd.getCreatedTask();
    }

    /** Undo the last command. */
    public String undoLastCommand() {
        return commandInvoker.undo();
    }

    /** Count available shelters. */
    public long countAvailableShelters() {
        return shelterRepository.findAvailableShelters().size();
    }
}
