package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.PlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository mockBookingRepository;
    @Mock
    private CustomerProfileRepository mockCustomerProfileRepository;
    @Mock
    private OrganizerProfileRepository mockOrganizerProfileRepository;
    @Mock
    private PlanRepository mockPlanRepository;

    @InjectMocks
    private BookingService bookingServiceUnderTest;

    @Test
    void testGetAvailablePlans() {
        // Setup
        final Plan plan = new Plan();
        plan.setId(0L);
        final OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(0L);
        final User user = new User();
        organizer.setUser(user);
        plan.setOrganizer(organizer);
        plan.setPlanName("planName");
        final List<Plan> plans = List.of(plan);
        when(mockPlanRepository.findByOrganizerIdOrderByUpdatedAtDesc(0L)).thenReturn(plans);

        // Run the test
        final List<Plan> result = bookingServiceUnderTest.getAvailablePlans(0L);

        // Verify the results
        assertThat(result).isEqualTo(plans);
    }

    @Test
    void testGetAvailablePlans_PlanRepositoryReturnsNoItems() {
        // Setup
        when(mockPlanRepository.findByOrganizerIdOrderByUpdatedAtDesc(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Plan> result = bookingServiceUnderTest.getAvailablePlans(0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetPlanDetail() {
        // Setup
        final Plan plan = new Plan();
        plan.setId(0L);
        final OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(0L);
        final User user = new User();
        organizer.setUser(user);
        plan.setOrganizer(organizer);
        plan.setPlanName("planName");
        when(mockPlanRepository.findById(0L)).thenReturn(Optional.of(plan));

        // Run the test
        final Plan result = bookingServiceUnderTest.getPlanDetail(0L);

        // Verify the results
        assertThat(result).isSameAs(plan);
    }

    @Test
    void testGetPlanDetail_PlanRepositoryReturnsNoItems() {
        // Setup
        when(mockPlanRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> bookingServiceUnderTest.getPlanDetail(0L))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConfirmBooking() {
        // Setup
        final LocalDate requestedDate = LocalDate.now().plusWeeks(2);

        when(mockBookingRepository.existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
                "email", 10L, requestedDate)).thenReturn(false);

        final CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setPasswordHash("passwordHash");
        customerProfile.setUser(user);
        when(mockCustomerProfileRepository.findByUserEmail("email")).thenReturn(Optional.of(customerProfile));

        final OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setId(0L);
        final User user1 = new User();
        user1.setId(0L);
        user1.setEmail("email");
        user1.setPasswordHash("passwordHash");
        organizerProfile.setUser(user1);

        final Plan plan = new Plan();
        plan.setId(10L);
        plan.setOrganizer(organizerProfile);
        plan.setPlanName("planName");
        plan.setPrice(new BigDecimal("123.45"));
        when(mockPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        // Configure BookingRepository.save(...).
        final Booking booking = new Booking();
        final CustomerProfile customerProfile1 = new CustomerProfile();
        booking.setCustomerProfile(customerProfile1);
        final OrganizerProfile organizerProfile1 = new OrganizerProfile();
        booking.setOrganizerProfile(organizerProfile1);
        booking.setPlan(plan);
        booking.setEventDate(requestedDate);
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setPlannerName("organizerName");
        booking.setPrice(new BigDecimal("123.45"));
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(booking);

        // Run the test
        final Booking result = bookingServiceUnderTest.confirmBooking(
                10L,
                requestedDate,
                "Wedding",
                "Outdoor ceremony for 80 guests",
                "email");

        // Verify the results
        assertThat(result).isSameAs(booking);
    }

    @Test
    void testConfirmBooking_CustomerProfileRepositoryReturnsNoItems() {
        // Setup
        final LocalDate requestedDate = LocalDate.now().plusWeeks(2);

        when(mockBookingRepository.existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
                "email", 10L, requestedDate)).thenReturn(false);

        final Plan plan = new Plan();
        plan.setId(10L);
        final OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setId(1L);
        plan.setOrganizer(organizerProfile);
        when(mockPlanRepository.findById(10L)).thenReturn(Optional.of(plan));
        when(mockCustomerProfileRepository.findByUserEmail("email")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(
                () -> bookingServiceUnderTest.confirmBooking(
                        10L,
                        requestedDate,
                        "Wedding",
                        "Outdoor ceremony for 80 guests",
                        "email"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConfirmBooking_PlanHasNoOrganizer() {
        // Setup
        final LocalDate requestedDate = LocalDate.now().plusWeeks(2);

        when(mockBookingRepository.existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
                "email", 10L, requestedDate)).thenReturn(false);

        final CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setId(0L);
        final User user = new User();
        user.setId(0L);
        user.setEmail("email");
        user.setPasswordHash("passwordHash");
        customerProfile.setUser(user);
        when(mockCustomerProfileRepository.findByUserEmail("email")).thenReturn(Optional.of(customerProfile));

        final Plan plan = new Plan();
        plan.setId(10L);
        when(mockPlanRepository.findById(10L)).thenReturn(Optional.of(plan));

        // Run the test
        assertThatThrownBy(
                () -> bookingServiceUnderTest.confirmBooking(
                        10L,
                        requestedDate,
                        "Wedding",
                        "Outdoor ceremony for 80 guests",
                        "email"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testConfirmBooking_EventDateTooSoon() {
        // Run the test
        assertThatThrownBy(
                () -> bookingServiceUnderTest.confirmBooking(
                        10L,
                        LocalDate.now().plusDays(6),
                        "Wedding",
                        "Outdoor ceremony for 80 guests",
                        "email"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Event date must be at least one week from today");
    }

    @Test
    void testConfirmBooking_DuplicateBookingRequest() {
        // Setup
        final LocalDate requestedDate = LocalDate.now().plusWeeks(2);
        when(mockBookingRepository.existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
                "email", 10L, requestedDate)).thenReturn(true);

        // Run the test
        assertThatThrownBy(
                () -> bookingServiceUnderTest.confirmBooking(
                        10L,
                        requestedDate,
                        "Wedding",
                        "Outdoor ceremony for 80 guests",
                        "email"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You already have a booking request for this plan and date");
    }
}
