package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.PlanRepository;
import org.springframework.stereotype.Service;

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

    public OrganizerProfile getOrganizerByEmail(String email) {
        return organizerProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Organizer profile not found for email: " + email));
    }

    // Create a new plan
    public Plan createPlan(String organizerEmail,
                           String planName,
                           BigDecimal price,
                             String description) {
        OrganizerProfile organizer = getOrganizerByEmail(organizerEmail);

        Plan plan = new Plan();
        plan.setOrganizer(organizer);
        plan.setPlanName(planName);
        plan.setPrice(price);
        plan.setDescription(description);
        plan.setExpiresAt(LocalDateTime.now().plusDays(7));

        return planRepository.save(plan);
    }

    // Get all plans by organizer
    public List<Plan> getPlansByOrganizer(Long organizerId) {
        return planRepository.findByOrganizerId(organizerId);
    }

    public Plan getPlanForOrganizer(Long planId, String organizerEmail) {
        OrganizerProfile organizer = getOrganizerByEmail(organizerEmail);
        return planRepository.findByIdAndOrganizerId(planId, organizer.getId())
                .orElseThrow(() -> new RuntimeException("Plan not found for organizer: " + planId));
    }

    public Plan updatePlan(Long planId,
                           String organizerEmail,
                           String planName,
                           BigDecimal price,
                           String description) {
        Plan plan = getPlanForOrganizer(planId, organizerEmail);
        plan.setPlanName(planName);
        plan.setPrice(price);
        plan.setDescription(description);
        return planRepository.save(plan);
    }
}
