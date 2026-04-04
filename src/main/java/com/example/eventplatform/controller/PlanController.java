package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.security.UserPrincipal;
import com.example.eventplatform.service.PlanService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // Show all plans for an organizer
    @GetMapping("/organizer/{organizerId}")
    public String getPlansByOrganizer(
            @PathVariable Long organizerId,
            Model model) {
        List<Plan> plans = planService.getPlansByOrganizer(organizerId);
        model.addAttribute("plans", plans);
        model.addAttribute("organizerId", organizerId);
        return "plans";
    }

    @GetMapping("/manage")
    public String managePlans(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Long organizerId = planService.getOrganizerByUserId(principal.getUserId()).getId();
        List<Plan> plans = planService.getPlansByOrganizer(organizerId);
        model.addAttribute("plans", plans);
        model.addAttribute("organizerId", organizerId);
        return "plans";
    }

    @GetMapping("/new")
    public String showCreatePlanForm(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("editingPlan", null);
        return "plan-form";
    }

    @GetMapping("/{planId}/edit")
    public String showEditPlanForm(@PathVariable Long planId,
                                   @AuthenticationPrincipal UserPrincipal principal,
                                   Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("editingPlan", planService.getPlanForOrganizer(planId, principal.getUserId()));
        return "plan-form";
    }

    // Create a new plan
    @PostMapping("/create")
    public String createPlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam String planName,
            @RequestParam BigDecimal price,
            @RequestParam String description
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        planService.createPlan(principal.getUserId(), planName, price, description);
        return "redirect:/plans/manage";
    }

    @PostMapping("/{planId}/edit")
    public String updatePlan(@PathVariable Long planId,
                             @AuthenticationPrincipal UserPrincipal principal,
                             @RequestParam String planName,
                             @RequestParam BigDecimal price,
                             @RequestParam String description) {
        if (principal == null) {
            return "redirect:/login";
        }

        planService.updatePlan(planId, principal.getUserId(), planName, price, description);
        return "redirect:/plans/manage";
    }
}
