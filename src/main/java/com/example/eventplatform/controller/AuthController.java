package com.example.eventplatform.controller;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.service.CategoryService;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryService categoryService;

    public AuthController(UserRepository userRepository,
                          CustomerProfileRepository customerProfileRepository,
                          OrganizerProfileRepository organizerProfileRepository,
                          PasswordEncoder passwordEncoder,
                          CategoryService categoryService) {
        this.userRepository = userRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.organizerProfileRepository = organizerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryService = categoryService;
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
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
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
        profile.setPhone(phone);
        profile.setAddress(address);
        profile.setCity(city);
        profile.setCountry(country);
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
            @RequestParam(name = "categoryIds", required = false) List<Long> categoryIds,
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

        try {
            User user = createUser(email, password, UserRole.ORGANIZER);
            OrganizerProfile profile = new OrganizerProfile();
            profile.setUser(user);
            profile.setBusinessName(businessName);
            profile.setDescription(description);
            profile.setCategories(categoryService.getCategoriesByIds(categoryIds));
            profile.setPhone(phone);
            profile.setWebsite(website);
            profile.setAddress(address);
            profile.setAverageRating(0.0);
            organizerProfileRepository.save(profile);

            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            populateRegisterPage(model, "Organizer Register", "/organizer/register", "/register", "Customer");
            return "register";
        }
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
        model.addAttribute("categories", categoryService.getAllCategories());
    }
}
