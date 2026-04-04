package com.example.eventplatform.service;

import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.Review;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.ReviewRepository;
import com.example.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrganizerProfileRepository organizerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void submitReview_shouldSaveNewReviewAndUpdateAverageRating() {
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@test.com");

        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(10L);
        organizer.setBusinessName("Test Events Co");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(organizerProfileRepository.findById(10L)).thenReturn(Optional.of(organizer));
        when(reviewRepository.findByUserIdAndOrganizerId(1L, 10L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewRepository.findAverageRatingByOrganizerId(10L)).thenReturn(4.5);

        Review result = reviewService.submitReview(1L, 10L, "Great service", 5);

        assertNotNull(result);
        assertEquals("Great service", result.getReviewText());
        assertEquals(5, result.getRatingValue());
        assertEquals(user, result.getUser());
        assertEquals(organizer, result.getOrganizer());
        assertEquals(4.5, organizer.getAverageRating());

        verify(reviewRepository).save(any(Review.class));
        verify(organizerProfileRepository).save(organizer);
    }

    @Test
    void submitReview_shouldUpdateExistingReview() {
        User user = new User();
        user.setId(1L);

        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(10L);

        Review existingReview = new Review();
        existingReview.setId(100L);
        existingReview.setReviewText("Old review");
        existingReview.setRatingValue(3);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(organizerProfileRepository.findById(10L)).thenReturn(Optional.of(organizer));
        when(reviewRepository.findByUserIdAndOrganizerId(1L, 10L)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewRepository.findAverageRatingByOrganizerId(10L)).thenReturn(4.0);

        Review result = reviewService.submitReview(1L, 10L, "Updated review", 4);

        assertEquals(100L, result.getId());
        assertEquals("Updated review", result.getReviewText());
        assertEquals(4, result.getRatingValue());
        assertEquals(4.0, organizer.getAverageRating());

        verify(reviewRepository).save(existingReview);
        verify(organizerProfileRepository).save(organizer);
    }

    @Test
    void submitReview_shouldThrowException_whenReviewTextIsBlank() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reviewService.submitReview(1L, 10L, "   ", 5)
        );

        assertEquals("Review text cannot be empty", ex.getMessage());
        verify(reviewRepository, never()).save(any());
        verify(organizerProfileRepository, never()).save(any());
    }

    @Test
    void submitReview_shouldThrowException_whenRatingIsNull() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reviewService.submitReview(1L, 10L, "Good", null)
        );

        assertEquals("Rating must be between 1 and 5", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void submitReview_shouldThrowException_whenRatingIsTooLow() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reviewService.submitReview(1L, 10L, "Good", 0)
        );

        assertEquals("Rating must be between 1 and 5", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void submitReview_shouldThrowException_whenRatingIsTooHigh() {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reviewService.submitReview(1L, 10L, "Good", 6)
        );

        assertEquals("Rating must be between 1 and 5", ex.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void getReviewsByOrganizer_shouldReturnReviewList() {
        Review r1 = new Review();
        Review r2 = new Review();

        when(reviewRepository.findByOrganizerIdOrderByCreatedAtDesc(10L))
                .thenReturn(List.of(r1, r2));

        List<Review> result = reviewService.getReviewsByOrganizer(10L);

        assertEquals(2, result.size());
        verify(reviewRepository).findByOrganizerIdOrderByCreatedAtDesc(10L);
    }

    @Test
    void getUserReviewText_shouldReturnExistingReviewText() {
        Review review = new Review();
        review.setReviewText("Very professional");

        when(reviewRepository.findByUserIdAndOrganizerId(1L, 10L))
                .thenReturn(Optional.of(review));

        String result = reviewService.getUserReviewText(1L, 10L);

        assertEquals("Very professional", result);
    }

    @Test
    void getUserReviewText_shouldReturnEmptyString_whenNoReviewExists() {
        when(reviewRepository.findByUserIdAndOrganizerId(1L, 10L))
                .thenReturn(Optional.empty());

        String result = reviewService.getUserReviewText(1L, 10L);

        assertEquals("", result);
    }

    @Test
    void getUserRating_shouldReturnExistingRating() {
        Review review = new Review();
        review.setRatingValue(4);

        when(reviewRepository.findByUserIdAndOrganizerId(1L, 10L))
                .thenReturn(Optional.of(review));

        Integer result = reviewService.getUserRating(1L, 10L);

        assertEquals(4, result);
    }

    @Test
    void getUserRating_shouldReturnZero_whenNoRatingExists() {
        when(reviewRepository.findByUserIdAndOrganizerId(1L, 10L))
                .thenReturn(Optional.empty());

        Integer result = reviewService.getUserRating(1L, 10L);

        assertEquals(0, result);
    }
}
