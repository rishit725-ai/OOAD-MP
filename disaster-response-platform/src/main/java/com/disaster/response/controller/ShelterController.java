package com.disaster.response.controller;

import com.disaster.response.model.Shelter;
import com.disaster.response.model.ShelterStatus;
import com.disaster.response.service.ShelterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller — Shelter Management (Major Use Case 3).
 */
@Controller
@RequestMapping("/shelters")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("shelters", shelterService.findAll());
        model.addAttribute("openCount", shelterService.countByStatus(ShelterStatus.OPEN));
        model.addAttribute("totalOccupancy", shelterService.getTotalOccupancy());
        model.addAttribute("totalCapacity", shelterService.getTotalCapacity());
        return "shelters/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("shelter", new Shelter());
        model.addAttribute("statuses", ShelterStatus.values());
        return "shelters/form";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Shelter shelter, RedirectAttributes ra) {
        Shelter saved = shelterService.createShelter(shelter);
        ra.addFlashAttribute("success", "Shelter '" + saved.getName() + "' created.");
        return "redirect:/shelters";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Shelter shelter = shelterService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shelter not found: " + id));
        model.addAttribute("shelter", shelter);
        model.addAttribute("statuses", ShelterStatus.values());
        return "shelters/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Shelter shelter, RedirectAttributes ra) {
        shelter.setId(id);
        shelterService.updateShelter(shelter);
        ra.addFlashAttribute("success", "Shelter updated.");
        return "redirect:/shelters";
    }

    @PostMapping("/{id}/open")
    public String open(@PathVariable Long id, RedirectAttributes ra) {
        shelterService.openShelter(id);
        ra.addFlashAttribute("success", "Shelter opened.");
        return "redirect:/shelters";
    }

    @PostMapping("/{id}/close")
    public String close(@PathVariable Long id, RedirectAttributes ra) {
        shelterService.closeShelter(id);
        ra.addFlashAttribute("success", "Shelter closed.");
        return "redirect:/shelters";
    }

    @PostMapping("/{id}/occupancy")
    public String updateOccupancy(@PathVariable Long id,
                                  @RequestParam int delta,
                                  RedirectAttributes ra) {
        shelterService.updateOccupancy(id, delta);
        ra.addFlashAttribute("success", "Occupancy updated.");
        return "redirect:/shelters";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        shelterService.deleteById(id);
        ra.addFlashAttribute("success", "Shelter deleted.");
        return "redirect:/shelters";
    }
}
