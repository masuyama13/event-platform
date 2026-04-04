package com.example.eventplatform.service;

import com.example.eventplatform.entity.Category;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class OrganizerService {

    private final OrganizerProfileRepository organizerProfileRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public OrganizerService(OrganizerProfileRepository organizerProfileRepository,
                            UserRepository userRepository,
                            CategoryService categoryService) {
        this.organizerProfileRepository = organizerProfileRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    public List<OrganizerProfile> getAllOrganizers(Long categoryId) {
        return organizerProfileRepository.findAllByCategoryIdOrderByRatingDesc(categoryId);
    }

    public OrganizerProfile getOrganizerById(Long id) {
        return organizerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found with id: " + id));
    }

    public OrganizerProfile getOrganizerByUserId(Long userId) {
        return organizerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Organizer not found for user id: " + userId));
    }

    public void validateOrganizerInput(String businessName,
                                       String description,
                                       List<Long> categoryIds,
                                       String phone,
                                       String website,
                                       String address) {
        requireText(businessName, "Business name is required.");
        requireText(description, "Description is required.");
        requireText(phone, "Phone is required.");
        requireText(website, "Website is required.");
        requireText(address, "Address is required.");
        categoryService.getCategoriesByIds(categoryIds);
    }

    public OrganizerProfile createOrganizer(Long userId,
                                            String businessName,
                                            String description,
                                            List<Long> categoryIds,
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

        validateOrganizerInput(businessName, description, categoryIds, phone, website, address);
        Set<Category> categories = categoryService.getCategoriesByIds(categoryIds);

        OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setUser(user);
        organizerProfile.setBusinessName(businessName);
        organizerProfile.setDescription(description);
        organizerProfile.setCategories(categories);
        organizerProfile.setPhone(phone);
        organizerProfile.setWebsite(website);
        organizerProfile.setAddress(address);
        organizerProfile.setAverageRating(0.0);

        return organizerProfileRepository.save(organizerProfile);
    }

    public OrganizerProfile updateOrganizer(Long id,
                                            String businessName,
                                            String description,
                                            List<Long> categoryIds,
                                            String phone,
                                            String website,
                                            String address) {

        OrganizerProfile organizerProfile = getOrganizerById(id);
        validateOrganizerInput(businessName, description, categoryIds, phone, website, address);
        Set<Category> categories = categoryService.getCategoriesByIds(categoryIds);

        organizerProfile.setBusinessName(businessName);
        organizerProfile.setDescription(description);
        organizerProfile.setCategories(categories);
        organizerProfile.setPhone(phone);
        organizerProfile.setWebsite(website);
        organizerProfile.setAddress(address);

        return organizerProfileRepository.save(organizerProfile);
    }

    @Transactional
    public OrganizerProfile updateProfile(Long userId,
                                          String email,
                                          String businessName,
                                          String description,
                                          List<Long> categoryIds,
                                          String phone,
                                          String website,
                                          String address) {
        updateEmail(userId, email);

        OrganizerProfile organizerProfile = getOrganizerByUserId(userId);
        return updateOrganizer(
                organizerProfile.getId(),
                businessName,
                description,
                categoryIds,
                phone,
                website,
                address
        );
    }

    private void updateEmail(Long userId, String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required.");
        }

        String normalizedEmail = email.trim();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!user.getEmail().equals(normalizedEmail) && userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email is already in use.");
        }

        user.setEmail(normalizedEmail);
        userRepository.save(user);
    }

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }
}
