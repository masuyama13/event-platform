package com.example.eventplatform;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
            customerUser.setPasswordHash(passwordEncoder.encode("temp123"));
            customerUser.setRole(UserRole.CUSTOMER);
            userRepository.save(customerUser);

            // Create temporary customer profile
            CustomerProfile customer = new CustomerProfile();
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setPhone("123-456-7890");
            customer.setAddress("123 Main St");
            customer.setCity("New York");
            customer.setCountry("USA");
            customer.setUser(customerUser);
            customerProfileRepository.save(customer);

            System.out.println(">>> Temporary customer profile created");

            // Create temporary organizer user
            User organizerUser = new User();
            organizerUser.setEmail("organizer@test.com");
            organizerUser.setPasswordHash(passwordEncoder.encode("temp123"));
            organizerUser.setRole(UserRole.ORGANIZER);
            userRepository.save(organizerUser);

            // Create temporary organizer profile
            OrganizerProfile organizer = new OrganizerProfile();
            organizer.setBusinessName("Test Events Co");
            organizer.setPhone("987-654-3210");
            organizer.setAddress("456 Event St");
            organizer.setDescription("Test organizer");
            organizer.setCategories(java.util.Set.of(wedding, meeting));
            organizer.setWebsite("www.test.com");
            organizer.setAverageRating(0.0);
            organizer.setUser(organizerUser);
            organizerProfileRepository.save(organizer);

            System.out.println(">>> Temporary organizer profile created");

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

            for (int i = 0; i < plans.length; i++) {
                Plan plan = new Plan();
                plan.setOrganizer(organizer);
                plan.setPlanName(plans[i][0]);
                plan.setDescription(plans[i][1]);
                plan.setPrice(prices[i]);
                plan.setExpiresAt(LocalDateTime.now().plusDays(30));
                planRepository.save(plan);
            }

            System.out.println(">>> Temporary plans (Plan A to D) created");
        }
    }

    private Category ensureCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }
}
