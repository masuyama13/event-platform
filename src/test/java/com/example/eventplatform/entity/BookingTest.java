package com.example.eventplatform.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void getPlanName_shouldFallbackToAssociatedPlan() {
        Plan plan = new Plan();
        plan.setPlanName("Premium Wedding Plan");

        Booking booking = new Booking();
        booking.setPlan(plan);

        assertThat(booking.getPlanName()).isEqualTo("Premium Wedding Plan");
    }

    @Test
    void getPlanDescription_shouldPreferSnapshotOverAssociatedPlan() {
        Plan plan = new Plan();
        plan.setDescription("Current description");

        Booking booking = new Booking();
        booking.setPlan(plan);
        booking.setPlanDescription("Booked description snapshot");

        assertThat(booking.getPlanDescription()).isEqualTo("Booked description snapshot");
    }

    @Test
    void onCreate_shouldInitializeTimestamps() {
        Booking booking = new Booking();
        booking.setPrice(new BigDecimal("100.00"));

        booking.onCreate();

        assertThat(booking.getCreatedAt()).isNotNull();
        assertThat(booking.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_shouldRefreshUpdatedAt() {
        Booking booking = new Booking();
        booking.setUpdatedAt(LocalDateTime.of(2025, 1, 1, 10, 0));

        booking.onUpdate();

        assertThat(booking.getUpdatedAt()).isAfter(LocalDateTime.of(2025, 1, 1, 10, 0));
    }
}
