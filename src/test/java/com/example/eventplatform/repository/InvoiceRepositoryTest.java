package com.example.eventplatform.repository;

import com.example.eventplatform.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Test
    void findByBookingId_shouldReturnInvoice() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPasswordHash("1234");
        user.setRole(UserRole.CUSTOMER);
        user = userRepository.save(user);

        CustomerProfile customer = new CustomerProfile();
        customer.setUser(user);
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setPhone("123-456-7890");
        customer.setAddress("Test Address");
        customer = customerProfileRepository.save(customer);

        User orgUser = new User();
        orgUser.setEmail("org@test.com");
        orgUser.setPasswordHash("1234");
        orgUser.setRole(UserRole.ORGANIZER);
        orgUser = userRepository.save(orgUser);

        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setBusinessName("Test Org");
        organizer.setDescription("Test Description");
        organizer.setPhone("111-222-3333");
        organizer.setWebsite("https://test.com");
        organizer.setAddress("Organizer Address");
        organizer.setAverageRating(0.0);
        organizer.setUser(orgUser);
        organizer = organizerProfileRepository.save(organizer);

        Plan plan = new Plan();
        plan.setOrganizer(organizer);
        plan.setPlanName("Test Plan");
        plan.setDescription("Test Plan Desc");
        plan.setPrice(new BigDecimal("100.00"));
        plan.setExpiresAt(LocalDateTime.now().plusDays(10));
        plan = planRepository.save(plan);

        Booking booking = new Booking();
        booking.setCustomerProfile(customer);
        booking.setOrganizerProfile(organizer);
        booking.setPlan(plan);
        booking.setPrice(new BigDecimal("100.00"));
        booking.setEventType("Test Event");
        booking.setEventDate(LocalDate.now());
        booking.setLocation("Test Location");
        booking.setRequestDetails("Test request");
        booking.setPlannerName("Test Planner");
        booking.setPlanName("Test Plan");
        booking.setPlanDescription("Test Description");
        booking.setStatus(BookingStatus.REQUESTED);
        booking = bookingRepository.save(booking);

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setTotalAmount(new BigDecimal("105.00"));
        invoice.setCurrency("cad");
        invoice.setStatus(InvoiceStatus.PENDING);
        invoiceRepository.save(invoice);

        Invoice found = invoiceRepository.findByBookingId(booking.getId());

        assertThat(found).isNotNull();
        assertThat(found.getTotalAmount()).isEqualTo(new BigDecimal("105.00"));
        assertThat(found.getStatus()).isEqualTo(InvoiceStatus.PENDING);
    }
}
