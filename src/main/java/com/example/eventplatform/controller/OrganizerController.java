package com.example.eventplatform.controller;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.service.OrganizerCategoryOptions;
import com.example.eventplatform.service.OrganizerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.security.Principal;

@Controller
@RequestMapping("/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;
    private final UserRepository userRepository;

    public OrganizerController(OrganizerService organizerService,
                               UserRepository userRepository) {
        this.organizerService = organizerService;
        this.userRepository = userRepository;
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

    @GetMapping("/profile/organizer")
    public String organizerProfilePage(Principal principal, Model model) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.ORGANIZER) {
            return "redirect:/organizers";
        }

        OrganizerProfile organizer = organizerService.getOrganizerByUserId(user.getId());
        model.addAttribute("organizer", organizer);
        model.addAttribute("isEdit", true);
        model.addAttribute("serviceCategories", OrganizerCategoryOptions.OPTIONS);
        return "organizer-form";
    }

    @PostMapping("/profile/organizer")
    public String updateOrganizer(Principal principal,
                                  @RequestParam String businessName,
                                  @RequestParam(required = false) String description,
                                  @RequestParam(required = false) String serviceCategory,
                                  @RequestParam(required = false) String phone,
                                  @RequestParam(required = false) String website,
                                  @RequestParam(required = false) String address,
                                  Model model) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.ORGANIZER) {
            return "redirect:/organizers";
        }

        try {
            organizerService.updateProfile(
                    user.getId(),
                    businessName,
                    description,
                    serviceCategory,
                    phone,
                    website,
                    address
            );
            return "redirect:/profile/organizer";

        } catch (RuntimeException e) {
            OrganizerProfile organizer = organizerService.getOrganizerByUserId(user.getId());
            model.addAttribute("organizer", organizer);
            model.addAttribute("isEdit", true);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serviceCategories", OrganizerCategoryOptions.OPTIONS);
            return "organizer-form";
        }
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User is not authenticated");
        }

        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
    }
}
