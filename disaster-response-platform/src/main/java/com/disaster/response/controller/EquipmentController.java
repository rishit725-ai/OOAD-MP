package com.disaster.response.controller;

import com.disaster.response.model.Equipment;
import com.disaster.response.model.EquipmentStatus;
import com.disaster.response.service.EquipmentService;
import com.disaster.response.service.TeamService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller — Equipment Management (Major Use Case 4).
 */
@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final TeamService teamService;

    public EquipmentController(EquipmentService equipmentService, TeamService teamService) {
        this.equipmentService = equipmentService;
        this.teamService = teamService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("equipmentList", equipmentService.findAll());
        model.addAttribute("availableCount", equipmentService.countByStatus(EquipmentStatus.AVAILABLE));
        model.addAttribute("inUseCount",     equipmentService.countByStatus(EquipmentStatus.IN_USE));
        return "equipment/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("equipment", new Equipment());
        model.addAttribute("statuses", EquipmentStatus.values());
        model.addAttribute("teams", teamService.findAll());
        return "equipment/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Equipment equipment,
                         @RequestParam(required = false) Long assignedTeamId,
                         RedirectAttributes ra) {
        equipment.setAvailableQuantity(equipment.getQuantity());
        equipment.setAssignedTeam(null); // clear any Spring binding attempt
        Equipment saved = equipmentService.createEquipment(equipment);
        if (assignedTeamId != null) {
            equipmentService.assignToTeam(saved.getId(), assignedTeamId);
        }
        ra.addFlashAttribute("success", "Equipment '" + saved.getName() + "' added.");
        return "redirect:/equipment";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Equipment equipment = equipmentService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + id));
        model.addAttribute("equipment", equipment);
        model.addAttribute("statuses", EquipmentStatus.values());
        model.addAttribute("teams", teamService.findAll());
        return "equipment/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @ModelAttribute Equipment equipment,
                         @RequestParam(required = false) Long assignedTeamId,
                         RedirectAttributes ra) {
        equipment.setId(id);
        equipment.setAssignedTeam(null);
        equipmentService.updateEquipment(equipment);
        if (assignedTeamId != null) {
            equipmentService.assignToTeam(id, assignedTeamId);
        }
        ra.addFlashAttribute("success", "Equipment updated.");
        return "redirect:/equipment";
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable Long id,
                         @RequestParam Long teamId,
                         RedirectAttributes ra) {
        equipmentService.assignToTeam(id, teamId);
        ra.addFlashAttribute("success", "Equipment assigned to team.");
        return "redirect:/equipment";
    }

    @PostMapping("/{id}/release")
    public String release(@PathVariable Long id, RedirectAttributes ra) {
        equipmentService.releaseFromTeam(id);
        ra.addFlashAttribute("success", "Equipment released.");
        return "redirect:/equipment";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam EquipmentStatus status,
                               RedirectAttributes ra) {
        equipmentService.updateStatus(id, status);
        ra.addFlashAttribute("success", "Equipment status updated.");
        return "redirect:/equipment";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        equipmentService.deleteById(id);
        ra.addFlashAttribute("success", "Equipment deleted.");
        return "redirect:/equipment";
    }
}
