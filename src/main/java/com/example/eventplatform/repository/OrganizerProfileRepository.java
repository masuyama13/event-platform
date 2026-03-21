package com.example.eventplatform.repository;

import com.example.eventplatform.entity.OrganizerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizerProfileRepository extends JpaRepository<OrganizerProfile, Long> {
    Optional<OrganizerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
