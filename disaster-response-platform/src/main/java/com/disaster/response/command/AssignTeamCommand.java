package com.disaster.response.command;

import com.disaster.response.model.*;
import com.disaster.response.repository.ResponseTeamRepository;
import com.disaster.response.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * COMMAND PATTERN — Concrete Command
 *
 * Assigns a ResponseTeam to an Incident by creating a Task record.
 * Undo removes the task and returns the team to AVAILABLE status.
 *
 * SRP: This class only handles team assignment/unassignment logic.
 */
public class AssignTeamCommand implements DisasterCommand {

    private static final Logger log = LoggerFactory.getLogger(AssignTeamCommand.class);

    private final Incident incident;
    private final ResponseTeam team;
    private final String strategyName;
    private final TaskRepository taskRepository;
    private final ResponseTeamRepository teamRepository;

    private Task createdTask; // held for undo

    public AssignTeamCommand(Incident incident, ResponseTeam team, String strategyName,
                             TaskRepository taskRepository, ResponseTeamRepository teamRepository) {
        this.incident = incident;
        this.team = team;
        this.strategyName = strategyName;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public void execute() {
        // Create task
        createdTask = new Task(incident, team, "Auto-assigned via " + strategyName, strategyName,
                incident.getSeverity().getPriorityLevel());
        taskRepository.save(createdTask);

        // Update team load and status
        team.setCurrentLoad(team.getCurrentLoad() + 1);
        team.setStatus(TeamStatus.DEPLOYED);
        teamRepository.save(team);

        log.info("Command executed: {} assigned to incident '{}'", team.getName(), incident.getTitle());
    }

    @Override
    public void undo() {
        if (createdTask != null) {
            createdTask.setStatus(TaskStatus.CANCELLED);
            taskRepository.save(createdTask);
        }

        // Return team capacity
        int newLoad = Math.max(0, team.getCurrentLoad() - 1);
        team.setCurrentLoad(newLoad);
        if (newLoad == 0) {
            team.setStatus(TeamStatus.AVAILABLE);
        }
        teamRepository.save(team);

        log.info("Command undone: {} unassigned from incident '{}'", team.getName(), incident.getTitle());
    }

    @Override
    public String getDescription() {
        return "Assigned team '" + team.getName() + "' to incident '" + incident.getTitle()
                + "' using " + strategyName;
    }

    @Override
    public String getCommandType() {
        return "ASSIGN_TEAM";
    }

    public Task getCreatedTask() {
        return createdTask;
    }
}
