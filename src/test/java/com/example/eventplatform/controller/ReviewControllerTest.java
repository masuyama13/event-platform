package com.example.eventplatform.controller;

import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.security.UserPrincipal;
import com.example.eventplatform.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void showReviewForm_shouldReturnReviewForm() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@test.com");
        user.setRole(UserRole.CUSTOMER);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(reviewService.getUserReviewText(1L, 10L)).thenReturn("Good organizer");
        when(reviewService.getUserRating(1L, 10L)).thenReturn(5);

        setAuthentication(user);

        mockMvc.perform(get("/reviews/organizer/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/review-form"))
                .andExpect(model().attribute("organizerId", 10L))
                .andExpect(model().attribute("existingReview", "Good organizer"))
                .andExpect(model().attribute("existingRating", 5));
    }

    @Test
    void submitReview_shouldRedirectToOrganizerDetail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@test.com");
        user.setRole(UserRole.CUSTOMER);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        setAuthentication(user);

        mockMvc.perform(post("/reviews/submit")
                        .with(csrf())
                        .param("organizerId", "10")
                        .param("reviewText", "Very helpful")
                        .param("ratingValue", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organizers/10"));

        verify(reviewService).submitReview(1L, 10L, "Very helpful", 5);
    }

    @Test
    void submitReview_shouldReturnForm_whenServiceThrowsException() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("customer@test.com");
        user.setRole(UserRole.CUSTOMER);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        doThrow(new RuntimeException("Review text cannot be empty"))
                .when(reviewService).submitReview(1L, 10L, "   ", 4);

        setAuthentication(user);

        mockMvc.perform(post("/reviews/submit")
                        .with(csrf())
                        .param("organizerId", "10")
                        .param("reviewText", "   ")
                        .param("ratingValue", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("customer/review-form"))
                .andExpect(model().attributeExists("error"));
    }

    private void setAuthentication(User user) {
        UserPrincipal principal = UserPrincipal.from(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
