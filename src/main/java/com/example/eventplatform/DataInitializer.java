package com.example.eventplatform;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.eventplatform.entity.User;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        // TODO: Remove when real authentication is ready

        if (userRepository.count() == 0) {

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
            organizer.setServiceCategory("Events");
            organizer.setWebsite("www.test.com");
            organizer.setAverageRating(0.0);
            organizer.setUser(organizerUser);
            organizerProfileRepository.save(organizer);

            System.out.println(">>> Temporary organizer profile created");
        }
    }
}
