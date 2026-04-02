package com.example.eventplatform.controller;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final OrganizerProfileRepository organizerProfileRepository;

    public ProfileController(UserRepository userRepository,
                             CustomerProfileRepository customerProfileRepository,
                             OrganizerProfileRepository organizerProfileRepository) {
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.organizerProfileRepository = organizerProfileRepository;
    }

    @GetMapping("/profile")
    public String profilePage(Principal principal, Model model) {
        User user = getCurrentUser(principal);
        model.addAttribute("user", user);

        if (user.getRole() == UserRole.CUSTOMER) {
            CustomerProfile customerProfile = customerProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Customer profile not found for user id: " + user.getId()));
            model.addAttribute("profileTitle", customerProfile.getFirstName() + " " + customerProfile.getLastName());
            model.addAttribute("customerProfile", customerProfile);
            model.addAttribute("editPath", "/profile/customer");
        } else {
            OrganizerProfile organizerProfile = organizerProfileRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Organizer profile not found for user id: " + user.getId()));
            model.addAttribute("profileTitle", organizerProfile.getBusinessName());
            model.addAttribute("organizerProfile", organizerProfile);
            model.addAttribute("editPath", "/profile/organizer");
        }

        return "profile";
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("User is not authenticated");
        }

        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
    }
}
