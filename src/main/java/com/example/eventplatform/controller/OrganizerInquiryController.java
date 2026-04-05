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
public class OrganizerInquiryController {

    private final String supportEmail;

    public OrganizerInquiryController(
            @Value("${app.support-email:support@example.com}") String supportEmail) {
        this.supportEmail = supportEmail;
    }

    @GetMapping("/organizer")
    public String inquiryForm(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal == null || principal.getAuthorities().stream().noneMatch(a -> "ROLE_ORGANIZER".equals(a.getAuthority()))) {
            return "redirect:/";
        }

        model.addAttribute("organizerEmail", principal.getUsername());
        model.addAttribute("supportEmail", supportEmail);
        return "organizer-inquiry-form";
    }
}
