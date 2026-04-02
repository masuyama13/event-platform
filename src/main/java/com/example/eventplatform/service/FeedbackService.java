package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Rating;
import com.example.eventplatform.entity.Review;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.RatingRepository;
import com.example.eventplatform.repository.ReviewRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedbackService {

    private final ReviewRepository reviewRepository;
    private final RatingRepository ratingRepository;
    private final OrganizerProfileRepository organizerProfileRepository;
    private final UserRepository userRepository;

    public FeedbackService(ReviewRepository reviewRepository,
                           RatingRepository ratingRepository,
                           OrganizerProfileRepository organizerProfileRepository,
                           UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.ratingRepository = ratingRepository;
        this.organizerProfileRepository = organizerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void submitFeedback(Long userId, Long organizerId, String reviewText, Double ratingValue) {
        if (reviewText == null || reviewText.trim().isEmpty()) {
            throw new RuntimeException("Review text cannot be empty");
        }

        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
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
        reviewRepository.save(review);

        Rating rating = ratingRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .orElse(new Rating());
        rating.setUser(user);
        rating.setOrganizer(organizer);
        rating.setRatingValue(ratingValue);
        ratingRepository.save(rating);

        updateAverageRating(organizer);
    }

    public String getUserReviewText(Long userId, Long organizerId) {
        return reviewRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .map(Review::getReviewText)
                .orElse("");
    }

    public Double getUserRating(Long userId, Long organizerId) {
        return ratingRepository.findByUserIdAndOrganizerId(userId, organizerId)
                .map(Rating::getRatingValue)
                .orElse(0.0);
    }

    private void updateAverageRating(OrganizerProfile organizer) {
        Double average = ratingRepository.findAverageRatingByOrganizerId(organizer.getId());
        double rounded = average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
        organizer.setAverageRating(rounded);
        organizerProfileRepository.save(organizer);
    }
}
