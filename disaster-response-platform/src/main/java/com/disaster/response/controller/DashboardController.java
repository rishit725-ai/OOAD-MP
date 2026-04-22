package com.disaster.response.controller;

import com.disaster.response.model.EquipmentStatus;
import com.disaster.response.model.IncidentStatus;
import com.disaster.response.model.ShelterStatus;
import com.disaster.response.model.TeamStatus;
import com.disaster.response.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC Controller for the Command Center Dashboard.
 * LoD: Calls service methods directly — never accesses repository data through services.
 */
@Controller
public class DashboardController {

    private final IncidentService incidentService;
    private final TeamService teamService;
    private final ShelterService shelterService;
    private final EquipmentService equipmentService;
    private final NotificationService notificationService;

    public DashboardController(IncidentService incidentService,
                               TeamService teamService,
                               ShelterService shelterService,
                               EquipmentService equipmentService,
                               NotificationService notificationService) {
        this.incidentService = incidentService;
        this.teamService = teamService;
        this.shelterService = shelterService;
        this.equipmentService = equipmentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        // Incident stats
        model.addAttribute("totalIncidents",   incidentService.countTotal());
        model.addAttribute("activeIncidents",  incidentService.findActive().size());
        model.addAttribute("criticalCount",    incidentService.countBySeverity(com.disaster.response.model.Severity.CRITICAL));
        model.addAttribute("resolvedToday",    incidentService.countByStatus(IncidentStatus.RESOLVED));
        model.addAttribute("recentIncidents",  incidentService.findActive());

        // Team stats
        model.addAttribute("totalTeams",      teamService.countTotal());
        model.addAttribute("availableTeams",  teamService.countByStatus(TeamStatus.AVAILABLE));
        model.addAttribute("deployedTeams",   teamService.countByStatus(TeamStatus.DEPLOYED));

        // Shelter stats
        model.addAttribute("totalShelters",    shelterService.countTotal());
        model.addAttribute("openShelters",     shelterService.countByStatus(ShelterStatus.OPEN));
        model.addAttribute("totalOccupancy",   shelterService.getTotalOccupancy());
        model.addAttribute("totalCapacity",    shelterService.getTotalCapacity());

        // Equipment stats
        model.addAttribute("totalEquipment",     equipmentService.countTotal());
        model.addAttribute("availableEquipment", equipmentService.countByStatus(EquipmentStatus.AVAILABLE));

        // Notifications
        model.addAttribute("unreadCount",       notificationService.countUnread());
        model.addAttribute("recentNotifications", notificationService.findUnread().stream().limit(5).toList());

        return "index";
    }
}
