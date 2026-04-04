package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.service.PlanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public String managePlans(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        Long organizerId = planService.getOrganizerByEmail(principal.getName()).getId();
        List<Plan> plans = planService.getPlansByOrganizer(organizerId);
        model.addAttribute("plans", plans);
        model.addAttribute("organizerId", organizerId);
        return "plans";
    }

    @GetMapping("/new")
    public String showCreatePlanForm(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("editingPlan", null);
        return "plan-form";
    }

    @GetMapping("/{planId}/edit")
    public String showEditPlanForm(@PathVariable Long planId,
                                   Principal principal,
                                   Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("editingPlan", planService.getPlanForOrganizer(planId, principal.getName()));
        return "plan-form";
    }

    // Create a new plan
    @PostMapping("/create")
    public String createPlan(
            Principal principal,
            @RequestParam String planName,
            @RequestParam BigDecimal price,
            @RequestParam String description
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        planService.createPlan(principal.getName(), planName, price, description);
        return "redirect:/plans/manage";
    }

    @PostMapping("/{planId}/edit")
    public String updatePlan(@PathVariable Long planId,
                             Principal principal,
                             @RequestParam String planName,
                             @RequestParam BigDecimal price,
                             @RequestParam String description) {
        if (principal == null) {
            return "redirect:/login";
        }

        planService.updatePlan(planId, principal.getName(), planName, price, description);
        return "redirect:/plans/manage";
    }
}
