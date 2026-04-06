package com.example.eventplatform.service;

import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.UserRepository;
import com.example.eventplatform.security.UserPrincipal;
import com.example.eventplatform.util.EmailNormalizer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail = EmailNormalizer.normalize(username);
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return UserPrincipal.from(user);
    }

    public UserDetails loadAdminByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail = EmailNormalizer.normalize(username);
        User user = userRepository.findByEmailAndRole(normalizedEmail, UserRole.ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found: " + username));
        return UserPrincipal.from(user);
    }

    public UserDetails loadNonAdminByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail = EmailNormalizer.normalize(username);
        User user = userRepository.findByEmailAndRoleNot(normalizedEmail, UserRole.ADMIN)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return UserPrincipal.from(user);
    }
}
