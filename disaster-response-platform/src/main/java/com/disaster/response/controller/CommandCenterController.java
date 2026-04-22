package com.disaster.response.controller;

import com.disaster.response.repository.CommandHistoryRepository;
import com.disaster.response.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller — Command Center (Minor Use Case 2).
 * Shows real-time audit log (Command pattern) and notifications (Observer pattern).
 */
@Controller
@RequestMapping("/command-center")
public class CommandCenterController {

    private final CommandHistoryRepository commandHistoryRepository;
    private final NotificationService notificationService;

    public CommandCenterController(CommandHistoryRepository commandHistoryRepository,
                                   NotificationService notificationService) {
        this.commandHistoryRepository = commandHistoryRepository;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String commandCenter(Model model) {
        model.addAttribute("commandHistory", commandHistoryRepository.findAllByOrderByExecutedAtDesc());
        model.addAttribute("notifications",  notificationService.findAll());
        model.addAttribute("unreadCount",    notificationService.countUnread());
        return "command-center/index";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable Long id, RedirectAttributes ra) {
        notificationService.markAsRead(id);
        ra.addFlashAttribute("success", "Notification marked as read.");
        return "redirect:/command-center";
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(RedirectAttributes ra) {
        notificationService.markAllAsRead();
        ra.addFlashAttribute("success", "All notifications marked as read.");
        return "redirect:/command-center";
    }
}
