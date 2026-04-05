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

import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // GET /booking/plans
    @GetMapping("/booking/plans")
    public String showPlans(@RequestParam Long organizerId, Model model) {
        List<Plan> plans = bookingService.getAvailablePlans(organizerId);
        model.addAttribute("plans", plans);
        model.addAttribute("organizerId", organizerId);
        return "plan-list";
    }

    // GET /plan/{planId}/book
    @GetMapping("/plan/{planId}/book")
    public String showBookingRequestForm(
            @PathVariable Long planId,
            Model model,
            HttpServletResponse response) {
        disableCaching(response);
        Plan selectedPlan = bookingService.getPlanDetail(planId);
        model.addAttribute("plan", selectedPlan);
        model.addAttribute("organizerId", selectedPlan.getOrganizer().getId());
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
        return new RedirectView("/booking/complete/" + savedBooking.getId());
    }

    // GET /booking/complete/{bookingId}
    @GetMapping("/booking/complete/{bookingId}")
    public String showBookingComplete(
            @PathVariable Long bookingId,
            Authentication authentication,
            Model model,
            HttpServletResponse response) {
        disableCaching(response);
        Booking booking = bookingService.getCustomerBooking(bookingId, authentication.getName());
        model.addAttribute("booking", booking);
        return "thankyou";
    }

    private void disableCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}
