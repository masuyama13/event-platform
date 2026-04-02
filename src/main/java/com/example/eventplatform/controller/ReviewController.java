package com.example.eventplatform.controller;

import com.example.eventplatform.service.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final FeedbackService feedbackService;

    // TODO: Replace with real user from authentication later
    private static final Long TEMP_CUSTOMER_USER_ID = 1L;

    public ReviewController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/organizer/{organizerId}")
    public String showReviewForm(@PathVariable Long organizerId, Model model) {
        String existingReview = feedbackService.getUserReviewText(TEMP_CUSTOMER_USER_ID, organizerId);
        Double existingRating = feedbackService.getUserRating(TEMP_CUSTOMER_USER_ID, organizerId);

        model.addAttribute("organizerId", organizerId);
        model.addAttribute("existingReview", existingReview);
        model.addAttribute("existingRating", existingRating);

        return "review-form";
    }

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long organizerId,
                               @RequestParam String reviewText,
                               @RequestParam Double ratingValue,
                               Model model) {
        try {
            feedbackService.submitFeedback(TEMP_CUSTOMER_USER_ID, organizerId, reviewText, ratingValue);
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
