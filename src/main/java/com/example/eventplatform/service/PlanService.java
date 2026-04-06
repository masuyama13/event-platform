package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.PlanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final OrganizerProfileRepository organizerProfileRepository;

    public PlanService(PlanRepository planRepository,
                       OrganizerProfileRepository organizerProfileRepository) {
        this.planRepository = planRepository;
        this.organizerProfileRepository = organizerProfileRepository;
    }

    public OrganizerProfile getOrganizerByUserId(Long userId) {
        return organizerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organizer profile not found for user id: " + userId));
    }

    // Create a new plan
    public Plan createPlan(Long organizerUserId,
                           String planName,
                           BigDecimal price,
                           String description,
                           LocalDateTime expiresAt) {
        OrganizerProfile organizer = getOrganizerByUserId(organizerUserId);

        Plan plan = new Plan();
        plan.setOrganizer(organizer);
        plan.setPlanName(planName);
        plan.setPrice(price);
        plan.setDescription(description);
        plan.setExpiresAt(expiresAt);

        return planRepository.save(plan);
    }

    // Get all plans by organizer
    public List<Plan> getPlansByOrganizer(Long organizerId) {
        return planRepository.findByOrganizerIdOrderByUpdatedAtDesc(organizerId);
    }

    public Plan getPlanForOrganizer(Long planId, Long organizerUserId) {
        OrganizerProfile organizer = getOrganizerByUserId(organizerUserId);
        return planRepository.findByIdAndOrganizerId(planId, organizer.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Plan not found for organizer: " + planId));
    }

    public Plan updatePlan(Long planId,
                           Long organizerUserId,
                           String planName,
                           BigDecimal price,
                           String description,
                           LocalDateTime expiresAt) {
        Plan plan = getPlanForOrganizer(planId, organizerUserId);
        plan.setPlanName(planName);
        plan.setPrice(price);
        plan.setDescription(description);
        plan.setExpiresAt(expiresAt);
        return planRepository.save(plan);
    }
}
