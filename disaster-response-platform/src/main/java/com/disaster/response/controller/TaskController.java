package com.disaster.response.controller;

import com.disaster.response.model.Task;
import com.disaster.response.model.TaskStatus;
import com.disaster.response.service.IncidentService;
import com.disaster.response.service.TaskAssignmentService;
import com.disaster.response.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MVC Controller — Task Assignment (Minor Use Case 1).
 * Exposes the Strategy pattern to the UI — user can choose which assignment strategy to use.
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskAssignmentService taskAssignmentService;
    private final IncidentService incidentService;
    private final TeamService teamService;

    public TaskController(TaskAssignmentService taskAssignmentService,
                          IncidentService incidentService,
                          TeamService teamService) {
        this.taskAssignmentService = taskAssignmentService;
        this.incidentService = incidentService;
        this.teamService = teamService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tasks", taskAssignmentService.getAllTasks());
        model.addAttribute("totalTasks", taskAssignmentService.getAllTasks().size());
        return "tasks/list";
    }

    @GetMapping("/assign")
    public String assignForm(Model model) {
        model.addAttribute("incidents", incidentService.findActive());
        model.addAttribute("teams", teamService.findAvailable());
        model.addAttribute("strategies", List.of(
                new String[]{"severityBasedStrategy", "Severity-Based (CRITICAL→largest team)"},
                new String[]{"proximityBasedStrategy", "Proximity-Based (nearest team)"},
                new String[]{"resourceBasedStrategy", "Resource-Based (most available capacity)"}
        ));
        return "tasks/assign";
    }

    @PostMapping("/auto-assign")
    public String autoAssign(@RequestParam Long incidentId,
                             @RequestParam(defaultValue = "severityBasedStrategy") String strategy,
                             RedirectAttributes ra) {
        Task task = taskAssignmentService.autoAssign(incidentId, strategy);
        if (task != null) {
            ra.addFlashAttribute("success",
                    "Auto-assigned: team '" + task.getAssignedTeamName() + "' → '" + task.getIncidentTitle() + "'");
        } else {
            ra.addFlashAttribute("error", "No available teams found for assignment.");
        }
        return "redirect:/tasks";
    }

    @PostMapping("/manual-assign")
    public String manualAssign(@RequestParam Long incidentId,
                               @RequestParam Long teamId,
                               @RequestParam(defaultValue = "severityBasedStrategy") String strategy,
                               RedirectAttributes ra) {
        Task task = taskAssignmentService.manualAssign(incidentId, teamId, strategy);
        if (task != null) {
            ra.addFlashAttribute("success", "Team assigned successfully.");
        } else {
            ra.addFlashAttribute("error", "Assignment failed.");
        }
        return "redirect:/tasks";
    }

    @PostMapping("/undo-last")
    public String undoLast(RedirectAttributes ra) {
        String undone = taskAssignmentService.undoLastAssignment();
        if (undone != null) {
            ra.addFlashAttribute("success", "Undone: " + undone);
        } else {
            ra.addFlashAttribute("error", "Nothing to undo.");
        }
        return "redirect:/tasks";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam TaskStatus status,
                               RedirectAttributes ra) {
        taskAssignmentService.updateTaskStatus(id, status);
        ra.addFlashAttribute("success", "Task status updated to: " + status.getDisplayName());
        return "redirect:/tasks";
    }
}
