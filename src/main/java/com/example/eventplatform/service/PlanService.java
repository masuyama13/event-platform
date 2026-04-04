package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    // TODO: Replace with actual authenticated organizer
    private OrganizerProfile getTemporaryOrganizer() {
        return organizerProfileRepository.findAll().get(0);
    }

    // Create a new plan
    public Plan createPlan(String planName, BigDecimal price,
                             String description) {

        OrganizerProfile organizer = getTemporaryOrganizer();

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
}
