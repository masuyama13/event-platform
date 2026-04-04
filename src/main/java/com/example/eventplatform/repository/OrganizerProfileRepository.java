package com.example.eventplatform.repository;

import com.example.eventplatform.entity.OrganizerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizerProfileRepository extends JpaRepository<OrganizerProfile, Long> {
    Optional<OrganizerProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    @Query("""
            select distinct o
            from OrganizerProfile o
            left join o.categories c
            where (:categoryId is null or c.id = :categoryId)
            order by o.averageRating desc, o.businessName asc
            """)
    List<OrganizerProfile> findAllByCategoryIdOrderByRatingDesc(@Param("categoryId") Long categoryId);
}
