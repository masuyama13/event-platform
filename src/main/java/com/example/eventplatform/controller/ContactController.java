package com.example.eventplatform.controller;

import com.example.eventplatform.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final String supportEmail;

    public ContactController(
            @Value("${app.support-email:support@example.com}") String supportEmail) {
        this.supportEmail = supportEmail;
    }

    @GetMapping("/organizer")
    public String organizerContactPage(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal == null || principal.getAuthorities().stream().noneMatch(a -> "ROLE_ORGANIZER".equals(a.getAuthority()))) {
            return "redirect:/";
        }

        model.addAttribute("organizerEmail", principal.getUsername());
        model.addAttribute("supportEmail", supportEmail);
        return "organizer-contact";
    }

    @GetMapping("/customer")
    public String customerContactPage(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal == null || principal.getAuthorities().stream().noneMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority()))) {
            return "redirect:/";
        }

        model.addAttribute("customerEmail", principal.getUsername());
        model.addAttribute("supportEmail", supportEmail);
        return "customer-contact";
    }
}
