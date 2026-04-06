package com.example.eventplatform.service;

import com.example.eventplatform.entity.Category;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizerServiceTest {

    @Mock
    private OrganizerProfileRepository organizerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private OrganizerService organizerService;

    @Test
    void createOrganizer_shouldRejectNonOrganizerUser() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.CUSTOMER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> organizerService.createOrganizer(
                1L, "Biz", "Desc", List.of(1L), "555", "https://example.com", "Addr"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User is not an organizer");
    }

    @Test
    void createOrganizer_shouldPersistNewProfile() {
        User user = new User();
        user.setId(1L);
        user.setEmail("organizer@test.com");
        user.setRole(UserRole.ORGANIZER);

        Category category = new Category("Wedding");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(organizerProfileRepository.existsByUserId(1L)).thenReturn(false);
        when(categoryService.getCategoriesByIds(List.of(10L))).thenReturn(Set.of(category));
        when(organizerProfileRepository.save(any(OrganizerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizerProfile result = organizerService.createOrganizer(
                1L, "Aurora Events", "Luxury weddings", List.of(10L), "555", "https://example.com", "Addr");

        assertThat(result.getUser()).isSameAs(user);
        assertThat(result.getBusinessName()).isEqualTo("Aurora Events");
        assertThat(result.getCategories()).containsExactly(category);
        assertThat(result.getAverageRating()).isEqualTo(0.0);
    }

    @Test
    void updateSponsorship_shouldRejectHalfFilledRange() {
        OrganizerProfile profile = new OrganizerProfile();
        profile.setId(10L);
        when(organizerProfileRepository.findById(10L)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> organizerService.updateSponsorship(10L, LocalDateTime.now(), null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Both sponsorship start and end are required.");
    }

    @Test
    void updateProfile_shouldNormalizeEmailAndDelegateProfileUpdate() {
        User user = new User();
        user.setId(1L);
        user.setEmail("before@test.com");
        user.setRole(UserRole.ORGANIZER);

        OrganizerProfile organizer = new OrganizerProfile();
        organizer.setId(99L);

        Category category = new Category("Wedding");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("after@test.com")).thenReturn(false);
        when(organizerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(organizer));
        when(organizerProfileRepository.findById(99L)).thenReturn(Optional.of(organizer));
        when(categoryService.getCategoriesByIds(List.of(10L))).thenReturn(Set.of(category));
        when(organizerProfileRepository.save(any(OrganizerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrganizerProfile result = organizerService.updateProfile(
                1L,
                " AFTER@test.com ",
                "Aurora Events",
                "Luxury weddings",
                List.of(10L),
                "555",
                "https://example.com",
                "Addr"
        );

        assertThat(user.getEmail()).isEqualTo("after@test.com");
        assertThat(result.getBusinessName()).isEqualTo("Aurora Events");
        assertThat(result.getCategories()).containsExactly(category);
        verify(userRepository).save(user);
    }
}
