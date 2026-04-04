package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock
    private PlanRepository mockPlanRepository;
    @Mock
    private OrganizerProfileRepository mockOrganizerProfileRepository;

    @InjectMocks
    private PlanService planServiceUnderTest;

    @Test
    void testCreatePlan() {
        // Setup
        // Configure OrganizerProfileRepository.findAll(...).
        final OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setPasswordHash("passwordHash");
        organizerProfile.setUser(user);
        final List<OrganizerProfile> organizerProfiles = List.of(organizerProfile);
        when(mockOrganizerProfileRepository.findAll()).thenReturn(organizerProfiles);

        // Configure PlanRepository.save(...).
        final Plan plan = new Plan();
        final OrganizerProfile organizer = new OrganizerProfile();
        plan.setOrganizer(organizer);
        plan.setPlanName("planName");
        plan.setDescription("description");
        plan.setPrice(new BigDecimal("0.00"));
        plan.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        when(mockPlanRepository.save(any(Plan.class))).thenReturn(plan);

        // Run the test
        final Plan result = planServiceUnderTest.createPlan("planName", new BigDecimal("0.00"), "description");

        // Verify the results
    }

    @Test
    void testCreatePlan_OrganizerProfileRepositoryReturnsNoItems() {
        // Setup
        when(mockOrganizerProfileRepository.findAll()).thenReturn(Collections.emptyList());

        // Configure PlanRepository.save(...).
        final Plan plan = new Plan();
        final OrganizerProfile organizer = new OrganizerProfile();
        plan.setOrganizer(organizer);
        plan.setPlanName("planName");
        plan.setDescription("description");
        plan.setPrice(new BigDecimal("0.00"));
        plan.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        when(mockPlanRepository.save(any(Plan.class))).thenReturn(plan);

        // Run the test
        final Plan result = planServiceUnderTest.createPlan("planName", new BigDecimal("0.00"), "description");

        // Verify the results
    }

    @Test
    void testGetPlansByOrganizer() {
        // Setup
        // Configure PlanRepository.findByOrganizerId(...).
        final Plan plan = new Plan();
        final OrganizerProfile organizer = new OrganizerProfile();
        plan.setOrganizer(organizer);
        plan.setPlanName("planName");
        plan.setDescription("description");
        plan.setPrice(new BigDecimal("0.00"));
        plan.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final List<Plan> plans = List.of(plan);
        when(mockPlanRepository.findByOrganizerId(0L)).thenReturn(plans);

        // Run the test
        final List<Plan> result = planServiceUnderTest.getPlansByOrganizer(0L);

        // Verify the results
    }

    @Test
    void testGetPlansByOrganizer_PlanRepositoryReturnsNoItems() {
        // Setup
        when(mockPlanRepository.findByOrganizerId(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Plan> result = planServiceUnderTest.getPlansByOrganizer(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
