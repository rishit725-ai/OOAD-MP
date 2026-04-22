package com.disaster.response.service;

import com.disaster.response.command.AssignTeamCommand;
import com.disaster.response.command.CommandInvoker;
import com.disaster.response.model.*;
import com.disaster.response.observer.IncidentEventPublisher;
import com.disaster.response.repository.IncidentRepository;
import com.disaster.response.repository.ResponseTeamRepository;
import com.disaster.response.repository.TaskRepository;
import com.disaster.response.strategy.TaskAssignmentStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * SRP: TaskAssignmentService is solely responsible for assigning teams to incidents.
 * Uses the Strategy pattern (injected strategies map) for dynamic algorithm selection.
 * Uses the Command pattern (CommandInvoker) for auditable, undoable assignment.
 * LoD: Communicates only with its immediate collaborators.
 */
@Service
@Transactional
public class TaskAssignmentService {

    private final Map<String, TaskAssignmentStrategy> strategies;
    private final CommandInvoker commandInvoker;
    private final IncidentEventPublisher eventPublisher;
    private final ResponseTeamRepository teamRepository;
    private final IncidentRepository incidentRepository;
    private final TaskRepository taskRepository;

    public TaskAssignmentService(
            Map<String, TaskAssignmentStrategy> strategies,
            CommandInvoker commandInvoker,
            IncidentEventPublisher eventPublisher,
            ResponseTeamRepository teamRepository,
            IncidentRepository incidentRepository,
            TaskRepository taskRepository) {
        this.strategies = strategies;
        this.commandInvoker = commandInvoker;
        this.eventPublisher = eventPublisher;
        this.teamRepository = teamRepository;
        this.incidentRepository = incidentRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Auto-assign the best team to an incident using the selected strategy.
     *
     * @param incidentId  the incident to assign
     * @param strategyKey bean name: "severityBasedStrategy", "proximityBasedStrategy", "resourceBasedStrategy"
     * @return the created Task, or null if no team available
     */
    public Task autoAssign(Long incidentId, String strategyKey) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        TaskAssignmentStrategy strategy = strategies.getOrDefault(
                strategyKey, strategies.get("severityBasedStrategy"));

        List<ResponseTeam> available = teamRepository.findAvailableTeams();
        ResponseTeam selectedTeam = strategy.assignTeam(incident, available);

        if (selectedTeam == null) return null;

        return executeAssignment(incident, selectedTeam, strategy.getStrategyName());
    }

    /**
     * Manually assign a specific team to an incident.
     */
    public Task manualAssign(Long incidentId, Long teamId, String strategyKey) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));
        ResponseTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));

        String strategyName = strategies.containsKey(strategyKey)
                ? strategies.get(strategyKey).getStrategyName()
                : "Manual Assignment";

        return executeAssignment(incident, team, strategyName);
    }

    private Task executeAssignment(Incident incident, ResponseTeam team, String strategyName) {
        AssignTeamCommand cmd = new AssignTeamCommand(
                incident, team, strategyName, taskRepository, teamRepository);
        commandInvoker.execute(cmd);

        // Update incident status to ASSIGNED
        if (incident.getStatus() == IncidentStatus.REPORTED) {
            incident.setStatus(IncidentStatus.ASSIGNED);
            incidentRepository.save(incident);
        }

        eventPublisher.publish(incident, "TEAM_ASSIGNED");
        return cmd.getCreatedTask();
    }

    public List<Task> getTasksForIncident(Long incidentId) {
        return taskRepository.findByIncidentIdOrderByAssignedAtDesc(incidentId);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAllByOrderByAssignedAtDesc();
    }

    public void updateTaskStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setStatus(newStatus);
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(java.time.LocalDateTime.now());
            // Reduce team load
            ResponseTeam team = task.getAssignedTeam();
            if (team != null) {
                team.setCurrentLoad(Math.max(0, team.getCurrentLoad() - 1));
                if (team.getCurrentLoad() == 0) team.setStatus(TeamStatus.AVAILABLE);
                teamRepository.save(team);
            }
        }
        taskRepository.save(task);
    }

    public String undoLastAssignment() {
        return commandInvoker.undo();
    }

    public List<String> getStrategyNames() {
        return strategies.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue().getStrategyName())
                .toList();
    }
}
