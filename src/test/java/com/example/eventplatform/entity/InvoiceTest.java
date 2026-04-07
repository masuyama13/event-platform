package com.example.eventplatform.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceTest {

    @Test
    void onCreate_shouldInitializeTimestamps() {
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(new BigDecimal("100.00"));

        invoice.onCreate();

        assertThat(invoice.getCreatedAt()).isNotNull();
        assertThat(invoice.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_shouldRefreshUpdatedAt() {
        Invoice invoice = new Invoice();
        invoice.setUpdatedAt(LocalDateTime.of(2025, 1, 1, 10, 0));

        invoice.onUpdate();

        assertThat(invoice.getUpdatedAt())
                .isAfter(LocalDateTime.of(2025, 1, 1, 10, 0));
    }
}
