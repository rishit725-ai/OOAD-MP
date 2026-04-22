package com.disaster.response.controller;

import com.disaster.response.dto.TeamFormDTO;
import com.disaster.response.model.ResponseTeam;
import com.disaster.response.model.TeamStatus;
import com.disaster.response.model.TeamType;
import com.disaster.response.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller — Team Management (Major Use Case 2).
 * Uses Factory pattern (via TeamService) to create team instances.
 * Demonstrates LSP: all team types handled through ResponseTeam interface.
 */
@Controller
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("teams", teamService.findAll());
        model.addAttribute("availableCount", teamService.findAvailable().size());
        return "teams/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("teamForm", new TeamFormDTO());
        model.addAttribute("teamTypes", TeamType.values());
        model.addAttribute("statuses", TeamStatus.values());
        return "teams/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute TeamFormDTO teamForm, RedirectAttributes ra) {
        ResponseTeam team = teamService.createTeam(teamForm);
        ra.addFlashAttribute("success", "Team '" + team.getName() + "' created successfully.");
        return "redirect:/teams/" + team.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model) {
        ResponseTeam team = teamService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + id));
        model.addAttribute("team", team);
        return "teams/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ResponseTeam team = teamService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + id));
        model.addAttribute("teamForm", teamService.toDTO(team));
        model.addAttribute("teamTypes", TeamType.values());
        model.addAttribute("statuses", TeamStatus.values());
        return "teams/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute TeamFormDTO teamForm, RedirectAttributes ra) {
        teamForm.setId(id);
        teamService.updateTeam(id, teamForm);
        ra.addFlashAttribute("success", "Team updated successfully.");
        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        teamService.deleteById(id);
        ra.addFlashAttribute("success", "Team deleted.");
        return "redirect:/teams";
    }
}
