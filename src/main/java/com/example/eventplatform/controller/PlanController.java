package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/plans")
public class PlanController {

    @Autowired
    private PlanService planService;

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

    // Create a new plan
    @PostMapping("/create")
    public String createPlan(
            @RequestParam String planName,
            @RequestParam BigDecimal price,
            @RequestParam String description
    ) {
        planService.createPlan(planName, price, description);
        return "redirect:/plans/organizer/1";
    }
}
