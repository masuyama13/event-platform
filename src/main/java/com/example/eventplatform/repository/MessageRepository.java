package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Get all messages between two users (in both directions)
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findMessagesBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    // Get unread messages for a user
    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);
}