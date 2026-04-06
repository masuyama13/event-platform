package com.example.eventplatform.service;

import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldNormalizeEmailBeforeLookup() {
        User user = user("customer@test.com", UserRole.CUSTOMER);
        when(userRepository.findByEmail("customer@test.com")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("  CUSTOMER@test.com ");

        assertThat(result.getUsername()).isEqualTo("customer@test.com");
    }

    @Test
    void loadAdminByUsername_shouldRejectMissingAdmin() {
        when(userRepository.findByEmailAndRole("admin@test.com", UserRole.ADMIN)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadAdminByUsername("admin@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Admin user not found: admin@test.com");
    }

    @Test
    void loadNonAdminByUsername_shouldReturnNonAdminPrincipal() {
        User user = user("organizer@test.com", UserRole.ORGANIZER);
        when(userRepository.findByEmailAndRoleNot("organizer@test.com", UserRole.ADMIN)).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadNonAdminByUsername("organizer@test.com");

        assertThat(result.getUsername()).isEqualTo("organizer@test.com");
    }

    private User user(String email, UserRole role) {
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(role);
        return user;
    }
}
