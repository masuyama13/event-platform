package com.example.eventplatform.service;

import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {

    @Mock
    private CustomerProfileRepository customerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerProfileService customerProfileService;

    @Test
    void updateProfile_shouldNormalizeEmailAndPersistProfileChanges() {
        User user = new User();
        user.setId(1L);
        user.setEmail("before@test.com");
        user.setRole(UserRole.CUSTOMER);

        CustomerProfile profile = new CustomerProfile();
        profile.setUser(user);
        profile.setFirstName("Before");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("after@test.com")).thenReturn(false);
        when(customerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(customerProfileRepository.save(any(CustomerProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerProfile result = customerProfileService.updateProfile(
                1L,
                "  AFTER@test.com ",
                "After",
                "Customer",
                "555-1234",
                "New Address"
        );

        assertThat(user.getEmail()).isEqualTo("after@test.com");
        assertThat(result.getFirstName()).isEqualTo("After");
        assertThat(result.getLastName()).isEqualTo("Customer");
        assertThat(result.getPhone()).isEqualTo("555-1234");
        assertThat(result.getAddress()).isEqualTo("New Address");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_shouldRejectBlankEmail() {
        assertThatThrownBy(() -> customerProfileService.updateProfile(1L, "   ", "A", "B", null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email is required.");
    }

    @Test
    void updateProfile_shouldRejectDuplicateEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("before@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("used@test.com")).thenReturn(true);

        assertThatThrownBy(() -> customerProfileService.updateProfile(1L, "used@test.com", "A", "B", null, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email is already in use.");
    }
}
