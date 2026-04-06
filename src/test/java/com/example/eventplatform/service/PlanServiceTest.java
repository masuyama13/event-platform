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
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        final OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("organizer@example.com");
        user.setPasswordHash("passwordHash");
        organizerProfile.setUser(user);
        when(mockOrganizerProfileRepository.findByUserId(0L))
                .thenReturn(Optional.of(organizerProfile));

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
        final Plan result = planServiceUnderTest.createPlan(
                0L,
                "planName",
                new BigDecimal("0.00"),
                "description",
                LocalDateTime.of(2030, 1, 1, 12, 0)
        );

        // Verify the results
        assertThat(result).isSameAs(plan);
    }

    @Test
    void testCreatePlan_OrganizerProfileRepositoryReturnsNoItems() {
        // Setup
        when(mockOrganizerProfileRepository.findByUserId(0L))
                .thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> planServiceUnderTest.createPlan(
                0L,
                "planName",
                new BigDecimal("0.00"),
                "description",
                LocalDateTime.of(2030, 1, 1, 12, 0)
        ))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testGetPlansByOrganizer() {
        // Setup
        // Configure PlanRepository.findByOrganizerIdOrderByUpdatedAtDesc(...).
        final Plan plan = new Plan();
        final OrganizerProfile organizer = new OrganizerProfile();
        plan.setOrganizer(organizer);
        plan.setPlanName("planName");
        plan.setDescription("description");
        plan.setPrice(new BigDecimal("0.00"));
        plan.setExpiresAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final List<Plan> plans = List.of(plan);
        when(mockPlanRepository.findByOrganizerIdOrderByUpdatedAtDesc(0L)).thenReturn(plans);

        // Run the test
        final List<Plan> result = planServiceUnderTest.getPlansByOrganizer(0L);

        // Verify the results
        assertThat(result).isEqualTo(plans);
    }

    @Test
    void testGetPlansByOrganizer_PlanRepositoryReturnsNoItems() {
        // Setup
        when(mockPlanRepository.findByOrganizerIdOrderByUpdatedAtDesc(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Plan> result = planServiceUnderTest.getPlansByOrganizer(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testUpdatePlan() {
        final OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(3L);
        final User user = new User();
        user.setId(7L);
        user.setEmail("organizer@example.com");
        organizer.setUser(user);

        final Plan plan = new Plan();
        plan.setId(5L);
        plan.setOrganizer(organizer);
        plan.setPlanName("before");
        plan.setDescription("before description");
        plan.setPrice(new BigDecimal("10.00"));
        plan.setExpiresAt(LocalDateTime.of(2030, 1, 1, 12, 0));

        when(mockOrganizerProfileRepository.findByUserId(7L))
                .thenReturn(Optional.of(organizer));
        when(mockPlanRepository.findByIdAndOrganizerId(5L, 3L))
                .thenReturn(Optional.of(plan));
        when(mockPlanRepository.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Plan result = planServiceUnderTest.updatePlan(
                5L,
                7L,
                "after",
                new BigDecimal("20.00"),
                "after description",
                LocalDateTime.of(2031, 2, 2, 13, 30)
        );

        assertThat(result.getPlanName()).isEqualTo("after");
        assertThat(result.getPrice()).isEqualByComparingTo("20.00");
        assertThat(result.getDescription()).isEqualTo("after description");
        assertThat(result.getExpiresAt()).isEqualTo(LocalDateTime.of(2031, 2, 2, 13, 30));
    }
}
