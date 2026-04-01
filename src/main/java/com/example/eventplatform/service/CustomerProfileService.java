package com.example.eventplatform.service;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileService(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }

    public CustomerProfile getByUserId(Long userId) {
        return customerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer profile not found for user id: " + userId));
    }

    public CustomerProfile updateProfile(Long userId,
                                         String firstName,
                                         String lastName,
                                         String phone,
                                         String address,
                                         String city,
                                         String country) {
        CustomerProfile profile = getByUserId(userId);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPhone(phone);
        profile.setAddress(address);
        profile.setCity(city);
        profile.setCountry(country);
        return customerProfileRepository.save(profile);
    }
}
