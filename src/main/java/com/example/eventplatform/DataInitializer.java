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

            System.out.println(">>> Temporary organizer profile created");
            System.out.println(">>> Temporary admin user created");

            // Create Plan A to D plans
            String[][] plans = {
                    {"Plan A", "Basic event package with standard decorations and setup."},
                    {"Plan B", "Standard event package with catering and photography."},
                    {"Plan C", "Premium event package with full catering, photography and DJ."},
                    {"Plan D", "Luxury event package with all-inclusive services and VIP setup."}
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

            createBooking(customer, organizer, savedPlans.get(0), BookingStatus.REQUESTED, LocalDate.now().plusDays(10));
            createBooking(customer, organizer, savedPlans.get(1), BookingStatus.APPROVED, LocalDate.now().plusDays(14));
            createBooking(customer, organizer, savedPlans.get(2), BookingStatus.REJECTED, LocalDate.now().plusDays(18));
            createBooking(customer, organizer, savedPlans.get(3), BookingStatus.CANCELLED, LocalDate.now().plusDays(22));

            System.out.println(">>> Temporary booking requests created");

            Booking completedBooking = createBooking(customer, organizer, savedPlans.get(0), BookingStatus.COMPLETED, LocalDate.now().plusDays(26));
            createPaidInvoice(completedBooking);
            System.out.println(">>> Temporary completed booking with paid invoice created");
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
