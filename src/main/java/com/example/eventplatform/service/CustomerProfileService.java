package com.example.eventplatform.service;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository,
                                  UserRepository userRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.userRepository = userRepository;
    }

    public CustomerProfile getByUserId(Long userId) {
        return customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found for user id: " + userId));
    }

    @Transactional
    public CustomerProfile updateProfile(Long userId,
                                         String email,
                                         String firstName,
                                         String lastName,
                                         String phone,
                                         String address,
                                         String city,
                                         String country) {
        updateEmail(userId, email);

        CustomerProfile profile = getByUserId(userId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPhone(phone);
        profile.setAddress(address);
        profile.setCity(city);
        profile.setCountry(country);
        return customerProfileRepository.save(profile);
    }

    private void updateEmail(Long userId, String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required.");
        }

        String normalizedEmail = email.trim();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found for user id: " + userId));

        if (!user.getEmail().equals(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email is already in use.");
        }

        user.setEmail(normalizedEmail);
        userRepository.save(user);
    }
}
