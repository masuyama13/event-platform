package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Review;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.ReviewRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         OrganizerProfileRepository organizerProfileRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.organizerProfileRepository = organizerProfileRepository;
        this.userRepository = userRepository;
    }

    public Review submitReview(Long userId, Long organizerId, String reviewText) {

        if (reviewText == null || reviewText.trim().isEmpty()) {
            throw new RuntimeException("Review text cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        OrganizerProfile organizer = organizerProfileRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException("Organizer not found: " + organizerId));

        Review review = reviewRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .orElse(new Review());

        review.setUser(user);
        review.setOrganizer(organizer);
        review.setReviewText(reviewText.trim());

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByOrganizer(Long organizerId) {
        return reviewRepository.findByOrganizerIdOrderByCreatedAtDesc(organizerId);
    }

    public String getUserReviewText(Long userId, Long organizerId) {
        return reviewRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .map(Review::getReviewText)
                .orElse("");
    }
}
