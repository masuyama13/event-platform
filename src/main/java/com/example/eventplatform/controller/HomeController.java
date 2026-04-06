package com.example.eventplatform.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CUSTOMER".equals(authority.getAuthority()))) {
            return "customer/home";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ORGANIZER".equals(authority.getAuthority()))) {
            return "organizer/home";
        }

        return "admin/home";
    }
}
