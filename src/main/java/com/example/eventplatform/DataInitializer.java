package com.example.eventplatform;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        if (userRepository.count() == 0) {
            Category wedding = ensureCategory("Wedding");
            Category meeting = ensureCategory("Meeting");
            Category workshop = ensureCategory("Workshop");
            ensureCategory("Conference");
            ensureCategory("Birthday Party");
            ensureCategory("Workshop");

            // Create temporary customer user
            User customerUser = new User();
            customerUser.setEmail("customer@test.com");
            customerUser.setPasswordHash(passwordEncoder.encode("temp1234"));
            customerUser.setRole(UserRole.CUSTOMER);
            userRepository.save(customerUser);

            // Create temporary customer profile
            CustomerProfile customer = new CustomerProfile();
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setPhone("123-456-7890");
            customer.setAddress("123 Main St");
            customer.setUser(customerUser);
            customerProfileRepository.save(customer);

            System.out.println(">>> Temporary customer profile created");

            // Create temporary organizer user
            User organizerUser = new User();
            organizerUser.setEmail("organizer@test.com");
            organizerUser.setPasswordHash(passwordEncoder.encode("temp1234"));
            organizerUser.setRole(UserRole.ORGANIZER);
            userRepository.save(organizerUser);

            User adminUser = new User();
            adminUser.setEmail("admin@test.com");
            adminUser.setPasswordHash(passwordEncoder.encode("temp1234"));
            adminUser.setRole(UserRole.ADMIN);
            userRepository.save(adminUser);

            // Create temporary organizer profile
            OrganizerProfile organizer = new OrganizerProfile();
            organizer.setBusinessName("Test Events Co");
            organizer.setPhone("987-654-3210");
            organizer.setAddress("456 Event St");
            organizer.setDescription("Test organizer");
            organizer.setCategories(java.util.Set.of(wedding, meeting));
            organizer.setWebsite("https://example.com");
            organizer.setAverageRating(0.0);
            organizer.setUser(organizerUser);
            organizerProfileRepository.save(organizer);

            User organizerUser2 = new User();
            organizerUser2.setEmail("organizer2@test.com");
            organizerUser2.setPasswordHash(passwordEncoder.encode("temp1234"));
            organizerUser2.setRole(UserRole.ORGANIZER);
            userRepository.save(organizerUser2);

            OrganizerProfile organizer2 = new OrganizerProfile();
            organizer2.setBusinessName("Sunset Celebrations");
            organizer2.setPhone("555-321-6789");
            organizer2.setAddress("789 Sunset Ave");
            organizer2.setDescription("Boutique organizer specializing in intimate celebrations and business socials.");
            organizer2.setCategories(java.util.Set.of(wedding, workshop, meeting));
            organizer2.setWebsite("https://sunset-celebrations.example.com");
            organizer2.setAverageRating(0.0);
            organizer2.setUser(organizerUser2);
            organizerProfileRepository.save(organizer2);

            System.out.println(">>> Temporary organizer profile created");
            System.out.println(">>> Temporary admin user created");

            // Create Plan A to D plans
            String[][] plans = {
                    {"Basic Plan", "Basic event package with standard decorations and setup. Up to 30 guests."},
                    {"Standard Plan", "Standard wedding package with catering and photography. Up to 20 guests."},
                    {"Premium Plan", "Premium wedding package with full catering, photography and DJ. Up to 20 guests."},
                    {"Luxury Plan", "Luxury wedding package with all-inclusive services and VIP setup. Up to 20 guests."}
            };

            BigDecimal[] prices = {
                    new BigDecimal("99.99"),
                    new BigDecimal("149.99"),
                    new BigDecimal("199.99"),
                    new BigDecimal("249.99")
            };

            List<Plan> savedPlans = new ArrayList<>();
            for (int i = 0; i < plans.length; i++) {
                Plan plan = new Plan();
                plan.setOrganizer(organizer);
                plan.setPlanName(plans[i][0]);
                plan.setDescription(plans[i][1]);
                plan.setPrice(prices[i]);
                plan.setExpiresAt(LocalDateTime.now().plusDays(30));
                savedPlans.add(planRepository.save(plan));
            }

            System.out.println(">>> Temporary plans (Plan A to D) created");

            Plan organizer2Plan = new Plan();
            organizer2Plan.setOrganizer(organizer2);
            organizer2Plan.setPlanName("Campaign Package");
            organizer2Plan.setDescription("This is a special launch discount plan for any events, up to 10 guests. Don’t miss out on this opportunity!");
            organizer2Plan.setPrice(new BigDecimal("9.99"));
            organizer2Plan.setExpiresAt(LocalDateTime.now().plusDays(30));
            planRepository.save(organizer2Plan);

            createBooking(customer, organizer, savedPlans.get(0), BookingStatus.REQUESTED, LocalDate.now().plusDays(10));
            createBooking(customer, organizer, savedPlans.get(1), BookingStatus.APPROVED, LocalDate.now().plusDays(14));
            createBooking(customer, organizer, savedPlans.get(2), BookingStatus.REJECTED, LocalDate.now().plusDays(18));
            createBooking(customer, organizer, savedPlans.get(3), BookingStatus.CANCELLED, LocalDate.now().plusDays(22));

            System.out.println(">>> Temporary booking requests created");

            Booking completedBooking = createBooking(customer, organizer, savedPlans.get(0), BookingStatus.CONFIRMED, LocalDate.now().plusDays(26));
            createPaidInvoice(completedBooking);
            System.out.println(">>> Temporary confirmed booking with paid invoice created");
        }
    }

    private Category ensureCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    private Booking createBooking(CustomerProfile customer,
                                  OrganizerProfile organizer,
                                  Plan plan,
                                  BookingStatus status,
                                  LocalDate eventDate) {
        Booking booking = new Booking();
        booking.setCustomerProfile(customer);
        booking.setOrganizerProfile(organizer);
        booking.setPlan(plan);
        booking.setPlannerName(organizer.getBusinessName());
        booking.setPlanName(plan.getPlanName());
        booking.setPlanDescription(plan.getDescription());
        booking.setPrice(plan.getPrice());
        booking.setEventType("Wedding");
        booking.setEventDate(eventDate);
        booking.setRequestDetails("Sample booking for status " + status.name().toLowerCase() + ".");
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    private void createPaidInvoice(Booking booking) {
        BigDecimal tax = booking.getPrice()
                .multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.HALF_UP);

        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setTotalAmount(booking.getPrice().add(tax).setScale(2, RoundingMode.HALF_UP));
        invoice.setCurrency("cad");
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setStripeSessionId("sess_demo_paid_" + booking.getId());
        invoice.setStripePaymentIntentId("pi_demo_paid_" + booking.getId());
        invoice.setPaidAt(LocalDateTime.now().minusDays(1));
        invoiceRepository.save(invoice);
    }
}
