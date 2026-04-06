package com.example.eventplatform.repository;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Review;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    @Test
    void findByOrganizerIdOrderByCreatedAtDesc_shouldReturnNewestReviewsFirst() {
        OrganizerProfile organizer = saveOrganizer("organizer@test.com");
        User firstUser = saveUser("first@test.com");
        User secondUser = saveUser("second@test.com");

        Review older = saveReview(firstUser, organizer, "Older", 4, LocalDateTime.of(2030, 1, 1, 10, 0));
        Review newer = saveReview(secondUser, organizer, "Newer", 5, LocalDateTime.of(2030, 1, 2, 10, 0));

        List<Review> reviews = reviewRepository.findByOrganizerIdOrderByCreatedAtDesc(organizer.getId());

        assertThat(reviews).extracting(Review::getId).containsExactly(newer.getId(), older.getId());
    }

    @Test
    void findAverageRatingByOrganizerId_shouldReturnAverageOfRatings() {
        OrganizerProfile organizer = saveOrganizer("organizer@test.com");
        User firstUser = saveUser("first@test.com");
        User secondUser = saveUser("second@test.com");

        saveReview(firstUser, organizer, "Great", 4, LocalDateTime.of(2030, 1, 1, 10, 0));
        saveReview(secondUser, organizer, "Excellent", 5, LocalDateTime.of(2030, 1, 2, 10, 0));

        Double average = reviewRepository.findAverageRatingByOrganizerId(organizer.getId());

        assertThat(average).isEqualTo(4.5);
    }

    private User saveUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(UserRole.CUSTOMER);
        return userRepository.save(user);
    }

    private OrganizerProfile saveOrganizer(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(UserRole.ORGANIZER);
        user = userRepository.save(user);

        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setUser(user);
        organizer.setBusinessName("Organizer");
        organizer.setDescription("Description");
        organizer.setPhone("555-1111");
        organizer.setWebsite("https://example.com");
        organizer.setAddress("123 Street");
        organizer.setAverageRating(0.0);
        return organizerProfileRepository.save(organizer);
    }

    private Review saveReview(User user,
                              OrganizerProfile organizer,
                              String text,
                              int rating,
                              LocalDateTime createdAt) {
        Review review = new Review();
        review.setUser(user);
        review.setOrganizer(organizer);
        review.setReviewText(text);
        review.setRatingValue(rating);
        review.setCreatedAt(createdAt);
        return reviewRepository.save(review);
    }
}
