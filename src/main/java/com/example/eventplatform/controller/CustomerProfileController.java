package com.example.eventplatform.controller;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.service.CustomerProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class CustomerProfileController {

    private final UserRepository userRepository;
    private final CustomerProfileService customerProfileService;

    public CustomerProfileController(UserRepository userRepository,
                                     CustomerProfileService customerProfileService) {
        this.userRepository = userRepository;
        this.customerProfileService = customerProfileService;
    }

    @GetMapping("/profile/customer")
    public String customerProfilePage(Principal principal, Model model) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.CUSTOMER) {
            return "redirect:/organizers";
        }

        model.addAttribute("customer", customerProfileService.getByUserId(user.getId()));
        return "customer-profile-form";
    }

    @PostMapping("/profile/customer")
    public String updateCustomerProfile(Principal principal,
                                        @RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam(required = false) String phone,
                                        @RequestParam(required = false) String address,
                                        @RequestParam(required = false) String city,
                                        @RequestParam(required = false) String country,
                                        Model model) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.CUSTOMER) {
            return "redirect:/organizers";
        }

        try {
            customerProfileService.updateProfile(user.getId(), firstName, lastName, phone, address, city, country);
            return "redirect:/profile/customer";
        } catch (RuntimeException e) {
            CustomerProfile profile = customerProfileService.getByUserId(user.getId());
            model.addAttribute("customer", profile);
            model.addAttribute("error", e.getMessage());
            return "customer-profile-form";
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
