package com.example.eventplatform.controller;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.security.UserPrincipal;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.service.CustomerProfileService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile/customer")
public class CustomerProfileController {

    private final UserRepository userRepository;
    private final CustomerProfileService customerProfileService;

    public CustomerProfileController(UserRepository userRepository,
                                     CustomerProfileService customerProfileService) {
        this.userRepository = userRepository;
        this.customerProfileService = customerProfileService;
    }

    @GetMapping
    public String customerProfilePage(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        User user = getCurrentUser(principal);
        if (user.getRole() != UserRole.CUSTOMER) {
            return "redirect:/organizers";
        }

        model.addAttribute("email", user.getEmail());
        model.addAttribute("customer", customerProfileService.getByUserId(user.getId()));
        return "customer-profile-form";
    }

    @PostMapping
    public String updateCustomerProfile(@AuthenticationPrincipal UserPrincipal principal,
                                        @RequestParam String email,
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
            customerProfileService.updateProfile(user.getId(), email, firstName, lastName, phone, address, city, country);
            refreshAuthentication(user.getId());
            return "redirect:/profile";
        } catch (RuntimeException e) {
            CustomerProfile profile = customerProfileService.getByUserId(user.getId());
            model.addAttribute("email", email);
            model.addAttribute("customer", profile);
            model.addAttribute("error", e.getMessage());
            return "customer-profile-form";
        }
    }

    private User getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new RuntimeException("User is not authenticated");
        }

        return userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getUserId()));
    }

    private void refreshAuthentication(Long userId) {
        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        UserPrincipal updatedPrincipal = UserPrincipal.from(updatedUser);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        updatedPrincipal,
                        updatedUser.getPasswordHash(),
                        updatedPrincipal.getAuthorities()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
