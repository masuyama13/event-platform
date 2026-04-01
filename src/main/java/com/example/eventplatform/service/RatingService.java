package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Rating;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.RatingRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    // Submit or update a rating
    public Rating submitRating(Long userId, Long organizerId, Double ratingValue) {

        if (ratingValue < 0 || ratingValue > 5) {
            throw new RuntimeException("Rating must be between 0 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(
                        "User not found: " + userId));

        OrganizerProfile organizer = organizerProfileRepository.findById(organizerId)
                .orElseThrow(() -> new RuntimeException(
                        "Organizer not found: " + organizerId));

        // Check if user already rated this organizer → update instead of create
        Rating rating = ratingRepository
                .findByUserIdAndOrganizerId(userId, organizerId)
                .orElse(new Rating());

        rating.setUser(user);
        rating.setOrganizer(organizer);
        rating.setRatingValue(ratingValue);

        ratingRepository.save(rating);

        // Recalculate and update average rating on organizer profile
        updateAverageRating(organizerId);

        return rating;
    }

    // Recalculate average rating and save to organizer profile
    public void updateAverageRating(Long organizerId) {
        Double average = ratingRepository
                .findAverageRatingByOrganizerId(organizerId);

        OrganizerProfile organizer = organizerProfileRepository
                .findById(organizerId)
                .orElseThrow(() -> new RuntimeException(
                        "Organizer not found: " + organizerId));

        // Round to 1 decimal place
        double rounded = average != null
                ? Math.round(average * 10.0) / 10.0
                : 0.0;

        organizer.setAverageRating(rounded);
        organizerProfileRepository.save(organizer);
    }

    // Get all ratings for an organizer
    public List<Rating> getRatingsByOrganizer(Long organizerId) {
        return ratingRepository.findByOrganizerId(organizerId);
    }

    // Get existing rating of a user for an organizer
    public Double getUserRating(Long userId, Long organizerId) {
        return ratingRepository
                .findByUserIdAndOrganizerId(userId, organizerId)
                .map(Rating::getRatingValue)
                .orElse(0.0);
    }
}
