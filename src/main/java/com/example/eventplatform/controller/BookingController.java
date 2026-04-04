package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // GET /booking/plans
    @GetMapping("/booking/plans")
    public String showPlans(Model model) {
        List<Plan> plans = bookingService.getAvailablePlans();
        model.addAttribute("plans", plans);
        return "plan-list";
    }

    // GET /plan/{planId}
    @GetMapping("/plan/{planId}")
    public String showPlanDetail(
            @PathVariable Long planId,
            Model model) {
        Plan selectedPlan = bookingService.getPlanDetail(planId);
        model.addAttribute("plan", selectedPlan);
        return "plan-detail";
    }

    // GET /plan/{planId}/book
    @GetMapping("/plan/{planId}/book")
    public String showBookingRequestForm(
            @PathVariable Long planId,
            Model model) {
        Plan selectedPlan = bookingService.getPlanDetail(planId);
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("earliestBookingDate", LocalDate.now().plusWeeks(1));
        return "booking-request";
    }

    // POST /bookings
    @PostMapping("/bookings")
    public RedirectView confirmBooking(
            @RequestParam Long planId,
            @RequestParam String eventDate,
            Authentication authentication) {

        Booking savedBooking = bookingService.confirmBooking(
                planId,
                LocalDate.parse(eventDate),
                authentication.getName()
        );
        return new RedirectView("/customer/bookings/" + savedBooking.getId());
    }
}
