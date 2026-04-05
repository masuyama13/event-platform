package com.example.eventplatform.controller;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.service.OrganizerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/sponsorships")
public class AdminSponsorshipController {

    private final OrganizerService organizerService;

    public AdminSponsorshipController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @GetMapping
    public String sponsorshipList(Model model) {
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("organizers", organizerService.getAllOrganizersForAdmin());
        return "admin-sponsorship-list";
    }

    @GetMapping("/{organizerId}")
    public String sponsorshipForm(@PathVariable Long organizerId, Model model) {
        model.addAttribute("organizer", organizerService.getOrganizerById(organizerId));
        return "admin-sponsorship-form";
    }

    @PostMapping("/{organizerId}")
    public String updateSponsorship(@PathVariable Long organizerId,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    LocalDateTime sponsoredFrom,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    LocalDateTime sponsoredUntil,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        try {
            organizerService.updateSponsorship(organizerId, sponsoredFrom, sponsoredUntil);
            redirectAttributes.addFlashAttribute("message", "Sponsorship updated.");
            return "redirect:/admin/sponsorships";
        } catch (RuntimeException exception) {
            OrganizerProfile organizer = organizerService.getOrganizerById(organizerId);
            organizer.setSponsoredFrom(sponsoredFrom);
            organizer.setSponsoredUntil(sponsoredUntil);
            model.addAttribute("organizer", organizer);
            model.addAttribute("error", exception.getMessage());
            return "admin-sponsorship-form";
        }
    }

    @PostMapping("/{organizerId}/clear")
    public String clearSponsorship(@PathVariable Long organizerId,
                                   RedirectAttributes redirectAttributes) {
        organizerService.updateSponsorship(organizerId, null, null);
        redirectAttributes.addFlashAttribute("message", "Sponsorship cleared.");
        return "redirect:/admin/sponsorships";
    }
}
