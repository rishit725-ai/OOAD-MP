package com.disaster.response.controller;

import com.disaster.response.model.Incident;
import com.disaster.response.model.IncidentStatus;
import com.disaster.response.model.Severity;
import com.disaster.response.service.IncidentService;
import com.disaster.response.service.TaskAssignmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller — Incident Management (Major Use Case 1).
 * SRP: Only handles HTTP request/response mapping for incidents.
 * LoD: Delegates all business logic to IncidentService.
 */
@Controller
@RequestMapping("/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final TaskAssignmentService taskAssignmentService;

    public IncidentController(IncidentService incidentService,
                              TaskAssignmentService taskAssignmentService) {
        this.incidentService = incidentService;
        this.taskAssignmentService = taskAssignmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("incidents", incidentService.findAll());
        model.addAttribute("activeCount", incidentService.findActive().size());
        return "incidents/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("incident", new Incident());
        model.addAttribute("severities", Severity.values());
        return "incidents/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Incident incident, RedirectAttributes ra) {
        Incident saved = incidentService.createIncident(incident);
        ra.addFlashAttribute("success", "Incident '" + saved.getTitle() + "' reported successfully.");
        return "redirect:/incidents/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        Incident incident = incidentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
        model.addAttribute("incident", incident);
        model.addAttribute("tasks", taskAssignmentService.getTasksForIncident(id));
        model.addAttribute("statuses", IncidentStatus.values());
        return "incidents/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Incident incident = incidentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + id));
        model.addAttribute("incident", incident);
        model.addAttribute("severities", Severity.values());
        model.addAttribute("statuses", IncidentStatus.values());
        return "incidents/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Incident incident, RedirectAttributes ra) {
        incident.setId(id);
        incidentService.updateIncident(incident);
        ra.addFlashAttribute("success", "Incident updated successfully.");
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam IncidentStatus status,
                               RedirectAttributes ra) {
        incidentService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Incident status updated to: " + status.getDisplayName());
        return "redirect:/incidents/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        incidentService.deleteById(id);
        ra.addFlashAttribute("success", "Incident deleted.");
        return "redirect:/incidents";
    }
}
