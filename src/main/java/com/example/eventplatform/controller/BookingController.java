package com.example.eventplatform.controller;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
        List<Booking> plans = bookingService.getAvailablePlans();
        model.addAttribute("plans", plans);
        return "planner";
    }

    // POST /booking/confirm
    @PostMapping("/confirm")
    public String showConfirmation(
            @RequestParam String planName,
            Model model) {
        Booking selectedPlan = bookingService.getPlanDetail(planName);
        model.addAttribute("booking", selectedPlan);
        return "confirmation";
    }

    // POST /booking/thankyou
    @PostMapping("/thankyou")
    public String confirmBooking(
            @RequestParam String planName,
            @RequestParam String organizerName,
            @RequestParam String eventDate,
            @RequestParam BigDecimal price,
            Model model) {

        Booking savedBooking = bookingService.confirmBooking(
                planName, organizerName, LocalDate.parse(eventDate), price
        );
        model.addAttribute("booking", savedBooking);
        return "thankyou";
    }
}
