package com.example.eventplatform.controller;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.service.OrganizerCategoryOptions;
import com.example.eventplatform.service.OrganizerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    // LIST
    @GetMapping
    public String showOrganizerList(Model model) {
        List<OrganizerProfile> organizers = organizerService.getAllOrganizers();
        model.addAttribute("organizers", organizers);
        return "organizer-list";
    }

    // DETAIL
    @GetMapping("/{id}")
    public String showOrganizerDetail(@PathVariable Long id, Model model) {
        OrganizerProfile organizer = organizerService.getOrganizerById(id);
        model.addAttribute("organizer", organizer);
        return "organizer-detail";
    }

    // CREATE SUBMIT
    @PostMapping
    public String createOrganizer(@RequestParam Long userId,
                                  @RequestParam String businessName,
                                  @RequestParam(required = false) String description,
                                  @RequestParam(required = false) String serviceCategory,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam(required = false) String website,
                                  @RequestParam(required = false) String address,
                                  Model model) {
        try {
            organizerService.createOrganizer(
                    userId,
                    businessName,
                    description,
                    serviceCategory,
                    phone,
                    website,
                    address
            );
            return "redirect:/organizers";
        } catch (RuntimeException e) {
            model.addAttribute("organizer", new OrganizerProfile());
            model.addAttribute("isEdit", false);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serviceCategories", OrganizerCategoryOptions.OPTIONS);
            return "organizer-form";
        }
    }

}
