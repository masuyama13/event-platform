package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Message;
import com.example.eventplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findMessagesBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    List<Message> findByReceiverIdAndIsReadFalse(Long receiverId);

    // NEW: Get all distinct users who have conversed with the organizer
    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver.id = :organizerUserId " +
            "UNION " +
            "SELECT DISTINCT m.receiver FROM Message m WHERE m.sender.id = :organizerUserId")
    List<User> findConversationPartners(@Param("organizerUserId") Long organizerUserId);

    // NEW: Get latest message between two users
    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
            "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
            "ORDER BY m.sentAt DESC")
    List<Message> findLatestMessageBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);
}