package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // GET /booking/plans
    @GetMapping("/plans")
    public String showPlans(Model model) {
        List<Plan> plans = bookingService.getAvailablePlans();
        model.addAttribute("plans", plans);
        return "planner";
    }

    // POST /booking/confirm
    @PostMapping("/confirm")
    public String showConfirmation(
            @RequestParam Long planId,
            Model model) {
        Plan selectedPlan = bookingService.getPlanDetail(planId);
        model.addAttribute("plan", selectedPlan);
        return "confirmation";
    }

    // POST /booking/thankyou
    @PostMapping("/thankyou")
    public String confirmBooking(
            @RequestParam Long planId,
            @RequestParam String eventDate,
            Authentication authentication,
            Model model) {

        Booking savedBooking = bookingService.confirmBooking(
                planId,
                LocalDate.parse(eventDate),
                authentication.getName()
        );
        model.addAttribute("booking", savedBooking);
        return "thankyou";
    }
}
