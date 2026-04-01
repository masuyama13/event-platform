package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Get all ratings for a specific organizer
    List<Rating> findByOrganizerId(Long organizerId);

    // Get rating by user and organizer (to check if user already rated)
    Optional<Rating> findByUserIdAndOrganizerId(Long userId, Long organizerId);

    // Calculate average rating for an organizer
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.organizer.id = :organizerId")
    Double findAverageRatingByOrganizerId(@Param("organizerId") Long organizerId);
}
