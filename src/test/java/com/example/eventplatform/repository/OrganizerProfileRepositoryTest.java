package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Category;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrganizerProfileRepositoryTest {

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findAllByCategoryIdOrderByRatingDesc_shouldFilterAndSortByRatingThenName() {
        Category wedding = categoryRepository.save(new Category("Wedding"));
        Category music = categoryRepository.save(new Category("Music"));

        OrganizerProfile higher = saveOrganizer("a@test.com", "Aurora Events", 4.9, wedding);
        OrganizerProfile lower = saveOrganizer("b@test.com", "Blue Events", 4.2, wedding);
        saveOrganizer("c@test.com", "City Sounds", 5.0, music);

        List<OrganizerProfile> organizers = organizerProfileRepository.findAllByCategoryIdOrderByRatingDesc(wedding.getId());

        assertThat(organizers).extracting(OrganizerProfile::getId).containsExactly(higher.getId(), lower.getId());
    }

    @Test
    void findSponsoredByCategoryId_shouldReturnCurrentlySponsoredOrganizersOnly() {
        Category wedding = categoryRepository.save(new Category("Wedding"));
        LocalDateTime now = LocalDateTime.of(2030, 1, 10, 12, 0);

        OrganizerProfile activeLaterEnd = saveOrganizer("a@test.com", "Aurora Events", 4.8, wedding);
        activeLaterEnd.setSponsoredFrom(now.minusDays(2));
        activeLaterEnd.setSponsoredUntil(now.plusDays(10));
        organizerProfileRepository.save(activeLaterEnd);

        OrganizerProfile activeSoonerEnd = saveOrganizer("b@test.com", "Blue Events", 4.5, wedding);
        activeSoonerEnd.setSponsoredFrom(now.minusDays(1));
        activeSoonerEnd.setSponsoredUntil(now.plusDays(3));
        organizerProfileRepository.save(activeSoonerEnd);

        OrganizerProfile expired = saveOrganizer("c@test.com", "Closed Events", 5.0, wedding);
        expired.setSponsoredFrom(now.minusDays(10));
        expired.setSponsoredUntil(now.minusDays(1));
        organizerProfileRepository.save(expired);

        List<OrganizerProfile> organizers = organizerProfileRepository.findSponsoredByCategoryId(wedding.getId(), now);

        assertThat(organizers).extracting(OrganizerProfile::getBusinessName)
                .containsExactly("Aurora Events", "Blue Events");
    }

    private OrganizerProfile saveOrganizer(String email, String businessName, double rating, Category category) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(UserRole.ORGANIZER);
        user = userRepository.save(user);

        OrganizerProfile profile = new OrganizerProfile();
        profile.setUser(user);
        profile.setBusinessName(businessName);
        profile.setDescription("Organizer description");
        profile.setCategories(new LinkedHashSet<>(List.of(category)));
        profile.setPhone("555-1111");
        profile.setWebsite("https://example.com");
        profile.setAddress("123 Street");
        profile.setAverageRating(rating);
        return organizerProfileRepository.save(profile);
    }
}
