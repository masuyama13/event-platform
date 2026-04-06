package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Invoice findByBookingId(Long bookingId);
    List<Invoice> findByBookingIdIn(Collection<Long> bookingIds);
    Optional<Invoice> findByStripeSessionId(String stripeSessionId);

}
