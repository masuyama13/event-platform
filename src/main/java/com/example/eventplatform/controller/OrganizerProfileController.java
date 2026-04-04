package com.example.eventplatform.controller;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.service.CategoryService;
import com.example.eventplatform.service.OrganizerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile/organizer")
public class OrganizerProfileController {

    private final OrganizerService organizerService;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public OrganizerProfileController(OrganizerService organizerService,
                                      UserRepository userRepository,
                                      CategoryService categoryService) {
        this.organizerService = organizerService;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String organizerProfilePage(Principal principal, Model model) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.ORGANIZER) {
            return "redirect:/organizers";
        }

        model.addAttribute("organizer", organizerService.getOrganizerByUserId(user.getId()));
        model.addAttribute("isEdit", true);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "organizer-form";
    }

    @PostMapping
    public String updateOrganizerProfile(Principal principal,
                                         @RequestParam String businessName,
                                         @RequestParam String description,
                                         @RequestParam(name = "categoryIds") List<Long> categoryIds,
                                         @RequestParam String phone,
                                         @RequestParam String website,
                                         @RequestParam String address,
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
                    categoryIds,
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
            model.addAttribute("categories", categoryService.getAllCategories());
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
