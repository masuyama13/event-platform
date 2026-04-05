package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    // Get all plans by organizer
    List<Plan> findByOrganizerId(Long organizerId);

    List<Plan> findByOrganizerIdOrderByUpdatedAtDesc(Long organizerId);

    // Get plan by organizer and plan name
    List<Plan> findByOrganizerIdAndPlanName(Long organizerId, String planName);

    Optional<Plan> findByIdAndOrganizerId(Long id, Long organizerId);
}
