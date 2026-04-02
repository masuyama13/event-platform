package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);

    Optional<Review> findByUserIdAndOrganizerId(Long userId, Long organizerId);
}
