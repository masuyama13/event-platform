package com.example.eventplatform.controller;

import com.example.eventplatform.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // TODO: Replace with real user from authentication later
    private final Long TEMP_CUSTOMER_USER_ID = 1L;

    // Show rating form for an organizer
    @GetMapping("/organizer/{organizerId}")
    public String showRatingForm(
            @PathVariable Long organizerId,
            Model model) {

        // Get existing rating if user already rated this organizer
        Double existingRating = ratingService
                .getUserRating(TEMP_CUSTOMER_USER_ID, organizerId);

        model.addAttribute("organizerId", organizerId);
        model.addAttribute("existingRating", existingRating);

        return "rating-form";
    }

    // Submit rating
    @PostMapping("/submit")
    public String submitRating(
            @RequestParam Long organizerId,
            @RequestParam Double ratingValue) {

        ratingService.submitRating(
                TEMP_CUSTOMER_USER_ID, organizerId, ratingValue);

        return "redirect:/organizers";
    }
}