package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository
        extends JpaRepository<Message, Long> {
    // Get all messages for a specific booking
    List<Message> findByBookingIdOrderBySentAtAsc(Long bookingId);

    // Get unread messages for a user
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}
