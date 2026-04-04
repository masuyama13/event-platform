package com.example.eventplatform.controller;

import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.service.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService,
                            UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
        return user.getId();
    }

    @GetMapping("/organizer/{organizerId}")
    public String showReviewForm(@PathVariable Long organizerId,
                                 Principal principal,
                                 Model model) {

        Long currentUserId = getCurrentUserId(principal);

        String existingReview = reviewService.getUserReviewText(currentUserId, organizerId);
        Integer existingRating = reviewService.getUserRating(currentUserId, organizerId);

        model.addAttribute("organizerId", organizerId);
        model.addAttribute("existingReview", existingReview);
        model.addAttribute("existingRating", existingRating);

        return "review-form";
    }

    @PostMapping("/submit")
    public String submitReview(@RequestParam Long organizerId,
                               @RequestParam String reviewText,
                               @RequestParam Integer ratingValue,
                               Principal principal,
                               Model model) {

        Long currentUserId = getCurrentUserId(principal);

        try {
            reviewService.submitReview(currentUserId, organizerId, reviewText, ratingValue);
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
