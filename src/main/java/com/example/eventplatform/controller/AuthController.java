package com.example.eventplatform.controller;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.service.OrganizerCategoryOptions;
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
    public String customerRegisterPage(Model model) {
        populateRegisterPage(model, "Customer Register", "/register", "/organizer/register", "Organizer");
        return "register";
    }

    @PostMapping("/register")
    public String registerCustomer(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            Model model
    ) {
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use.");
            populateRegisterPage(model, "Customer Register", "/register", "/organizer/register", "Organizer");
            return "register";
        }

        User user = createUser(email, password, UserRole.CUSTOMER);
        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        customerProfileRepository.save(profile);

        return "redirect:/login";
    }

    @GetMapping("/organizer/register")
    public String organizerRegisterPage(Model model) {
        populateRegisterPage(model, "Organizer Register", "/organizer/register", "/register", "Customer");
        return "register";
    }

    @PostMapping("/organizer/register")
    public String registerOrganizer(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String businessName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String serviceCategory,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String address,
            Model model
    ) {
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email is already in use.");
            populateRegisterPage(model, "Organizer Register", "/organizer/register", "/register", "Customer");
            return "register";
        }

        if (!OrganizerCategoryOptions.isValid(serviceCategory)) {
            model.addAttribute("error", "Please select a valid category.");
            populateRegisterPage(model, "Organizer Register", "/organizer/register", "/register", "Customer");
            return "register";
        }

        User user = createUser(email, password, UserRole.ORGANIZER);
        OrganizerProfile profile = new OrganizerProfile();
        profile.setUser(user);
        profile.setBusinessName(businessName);
        profile.setDescription(description);
        profile.setServiceCategory(serviceCategory);
        profile.setPhone(phone);
        profile.setWebsite(website);
        profile.setAddress(address);
        profile.setAverageRating(0.0);
        organizerProfileRepository.save(profile);

        return "redirect:/login";
    }

    private User createUser(String email, String password, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private void populateRegisterPage(Model model,
                                      String pageTitle,
                                      String formAction,
                                      String alternateRegisterPath,
                                      String alternateRegisterLabel) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("formAction", formAction);
        model.addAttribute("alternateRegisterPath", alternateRegisterPath);
        model.addAttribute("alternateRegisterLabel", alternateRegisterLabel);
        model.addAttribute("serviceCategories", OrganizerCategoryOptions.OPTIONS);
    }
}
