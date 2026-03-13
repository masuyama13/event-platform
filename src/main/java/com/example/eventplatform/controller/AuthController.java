package com.example.eventplatform.controller;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          CustomerProfileRepository customerProfileRepository,
                          OrganizerProfileRepository organizerProfileRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.organizerProfileRepository = organizerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String businessName,
            Model model
    ) {
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use.");
            return "register";
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(UserRole.valueOf(role));
        userRepository.save(user);

        if (user.getRole() == UserRole.CUSTOMER) {
            CustomerProfile profile = new CustomerProfile();
            profile.setUser(user);
            profile.setFirstName(firstName);
            profile.setLastName(lastName);
            customerProfileRepository.save(profile);
        } else if (user.getRole() == UserRole.ORGANIZER) {
            OrganizerProfile profile = new OrganizerProfile();
            profile.setUser(user);
            profile.setBusinessName(businessName);
            organizerProfileRepository.save(profile);
        }

        return "redirect:/login";
    }
}
