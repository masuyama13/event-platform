package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.BookingStatus;
import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Plan;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void findByCustomerProfileUserEmailOrderByUpdatedAtDesc_shouldReturnNewestFirst() {
        CustomerProfile customer = saveCustomer("customer@test.com");
        OrganizerProfile organizer = saveOrganizer("organizer@test.com", "Bright Events");
        Plan firstPlan = savePlan(organizer, "Basic");
        Plan secondPlan = savePlan(organizer, "Premium");

        Booking older = saveBooking(customer, organizer, firstPlan, LocalDateTime.of(2030, 1, 2, 9, 0));
        Booking newer = saveBooking(customer, organizer, secondPlan, LocalDateTime.of(2030, 1, 3, 9, 0));

        List<Booking> bookings = bookingRepository.findByCustomerProfileUserEmailOrderByUpdatedAtDesc("customer@test.com");

        assertThat(bookings).extracting(Booking::getId).containsExactly(newer.getId(), older.getId());
    }

    @Test
    void existsByCustomerProfileUserEmailAndPlanIdAndEventDate_shouldMatchExistingRequest() {
        CustomerProfile customer = saveCustomer("customer@test.com");
        OrganizerProfile organizer = saveOrganizer("organizer@test.com", "Bright Events");
        Plan plan = savePlan(organizer, "Premium");
        LocalDate eventDate = LocalDate.of(2030, 6, 1);

        saveBooking(customer, organizer, plan, LocalDateTime.of(2030, 1, 3, 9, 0), eventDate);

        boolean exists = bookingRepository.existsByCustomerProfileUserEmailAndPlanIdAndEventDate(
                "customer@test.com",
                plan.getId(),
                eventDate
        );

        assertThat(exists).isTrue();
    }

    private CustomerProfile saveCustomer(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(UserRole.CUSTOMER);
        user = userRepository.save(user);

        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setFirstName("Test");
        profile.setLastName("Customer");
        profile.setPhone("123");
        profile.setAddress("Customer Street");
        return customerProfileRepository.save(profile);
    }

    private OrganizerProfile saveOrganizer(String email, String businessName) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(UserRole.ORGANIZER);
        user = userRepository.save(user);

        OrganizerProfile profile = new OrganizerProfile();
        profile.setUser(user);
        profile.setBusinessName(businessName);
        profile.setDescription("Event organizer");
        profile.setPhone("555-1111");
        profile.setWebsite("https://example.com");
        profile.setAddress("Organizer Street");
        profile.setAverageRating(4.0);
        return organizerProfileRepository.save(profile);
    }

    private Plan savePlan(OrganizerProfile organizer, String planName) {
        Plan plan = new Plan();
        plan.setOrganizer(organizer);
        plan.setPlanName(planName);
        plan.setDescription(planName + " description");
        plan.setPrice(new BigDecimal("100.00"));
        plan.setExpiresAt(LocalDateTime.of(2031, 1, 1, 0, 0));
        return planRepository.save(plan);
    }

    private Booking saveBooking(CustomerProfile customer,
                                OrganizerProfile organizer,
                                Plan plan,
                                LocalDateTime updatedAt) {
        return saveBooking(customer, organizer, plan, updatedAt, LocalDate.of(2030, 6, 1));
    }

    private Booking saveBooking(CustomerProfile customer,
                                OrganizerProfile organizer,
                                Plan plan,
                                LocalDateTime updatedAt,
                                LocalDate eventDate) {
        Booking booking = new Booking();
        booking.setCustomerProfile(customer);
        booking.setOrganizerProfile(organizer);
        booking.setPlan(plan);
        booking.setEventType("Wedding");
        booking.setEventDate(eventDate);
        booking.setLocation("Vancouver");
        booking.setRequestDetails("Outdoor ceremony");
        booking.setStatus(BookingStatus.REQUESTED);
        booking.setPlannerName(organizer.getBusinessName());
        booking.setPlanName(plan.getPlanName());
        booking.setPlanDescription(plan.getDescription());
        booking.setPrice(plan.getPrice());
        booking.setUpdatedAt(updatedAt);
        return bookingRepository.save(booking);
    }
}
