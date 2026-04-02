package com.example.eventplatform.controller;

import com.example.eventplatform.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    // TODO: Replace with real user from authentication later
    private static final Long TEMP_CUSTOMER_USER_ID = 1L;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/organizer/{organizerId}")
    public String showReviewForm(@PathVariable Long organizerId, Model model) {
        String existingReview = reviewService.getUserReviewText(TEMP_CUSTOMER_USER_ID, organizerId);
        Integer existingRating = reviewService.getUserRating(TEMP_CUSTOMER_USER_ID, organizerId);

        model.addAttribute("organizerId", organizerId);
        model.addAttribute("existingReview", existingReview);
        model.addAttribute("existingRating", existingRating);

        return "review-form";
    }

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long organizerId,
                               @RequestParam String reviewText,
                               @RequestParam Integer ratingValue,
                               Model model) {
        try {
            reviewService.submitReview(TEMP_CUSTOMER_USER_ID, organizerId, reviewText, ratingValue);
            return "redirect:/organizers/" + organizerId;
        } catch (RuntimeException e) {
            model.addAttribute("organizerId", organizerId);
            model.addAttribute("existingReview", reviewText);
            model.addAttribute("existingRating", ratingValue);
            model.addAttribute("error", e.getMessage());
            return "review-form";
        }
    }
}
