package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerService {

    private final OrganizerProfileRepository organizerProfileRepository;
    private final UserRepository userRepository;

    public OrganizerService(OrganizerProfileRepository organizerProfileRepository,
                            UserRepository userRepository) {
        this.organizerProfileRepository = organizerProfileRepository;
        this.userRepository = userRepository;
    }

    public List<OrganizerProfile> getAllOrganizers() {
        return organizerProfileRepository.findAll();
    }

    public OrganizerProfile getOrganizerById(Long id) {
        return organizerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found with id: " + id));
    }

    public OrganizerProfile getOrganizerByUserId(Long userId) {
        return organizerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organizer not found for user id: " + userId));
    }

    public OrganizerProfile createOrganizer(Long userId,
                                            String businessName,
                                            String description,
                                            String serviceCategory,
                                            String phone,
                                            String website,
                                            String address) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (user.getRole() != UserRole.ORGANIZER) {
            throw new RuntimeException("User is not an organizer");
        }

        if (organizerProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("Organizer profile already exists for this user");
        }

        if (!OrganizerCategoryOptions.isValid(serviceCategory)) {
            throw new RuntimeException("Please select a valid category");
        }

        OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setUser(user);
        organizerProfile.setBusinessName(businessName);
        organizerProfile.setDescription(description);
        organizerProfile.setServiceCategory(serviceCategory);
        organizerProfile.setPhone(phone);
        organizerProfile.setWebsite(website);
        organizerProfile.setAddress(address);
        organizerProfile.setAverageRating(0.0);

        return organizerProfileRepository.save(organizerProfile);
    }

    public OrganizerProfile updateOrganizer(Long id,
                                            String businessName,
                                            String description,
                                            String serviceCategory,
                                            String phone,
                                            String website,
                                            String address) {

        OrganizerProfile organizerProfile = getOrganizerById(id);

        if (!OrganizerCategoryOptions.isValid(serviceCategory)) {
            throw new RuntimeException("Please select a valid category");
        }

        organizerProfile.setBusinessName(businessName);
        organizerProfile.setDescription(description);
        organizerProfile.setServiceCategory(serviceCategory);
        organizerProfile.setPhone(phone);
        organizerProfile.setWebsite(website);
        organizerProfile.setAddress(address);

        return organizerProfileRepository.save(organizerProfile);
    }
}
