package com.example.eventplatform.service;

import com.example.eventplatform.entity.*;
import com.example.eventplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private OrganizerProfileRepository organizerProfileRepository;

    // Existing - unchanged
    public List<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        return messageRepository.findMessagesBetweenUsers(userId1, userId2);
    }

    // Existing - unchanged
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found: " + senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found: " + receiverId));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        return messageRepository.save(message);
    }

    // Existing - unchanged
    public void markAsRead(Long currentUserId, Long otherUserId) {
        List<Message> unreadMessages = messageRepository
                .findByReceiverIdAndIsReadFalse(currentUserId);

        unreadMessages.forEach(message -> {
            if (message.getSender().getId().equals(otherUserId)) {
                message.setRead(true);
                messageRepository.save(message);
            }
        });
    }

    // Existing - unchanged
    public int getUnreadCount(Long receiverId) {
        return messageRepository
                .findByReceiverIdAndIsReadFalse(receiverId)
                .size();
    }

    // NEW: Get conversation list for organizer inbox
    public List<Map<String, Object>> getConversationListForOrganizer(Long organizerUserId) {
        List<User> partners = messageRepository.findConversationPartners(organizerUserId);

        List<Map<String, Object>> conversations = new ArrayList<>();

        for (User partner : partners) {
            Map<String, Object> entry = new HashMap<>();

            // Get customer profile for first name
            customerProfileRepository.findByUserId(partner.getId()).ifPresent(cp -> {
                entry.put("customerFirstName", cp.getFirstName());
                entry.put("customerUserId", partner.getId());
            });

            // Get latest message for date/time
            List<Message> latest = messageRepository
                    .findLatestMessageBetweenUsers(organizerUserId, partner.getId());
            if (!latest.isEmpty()) {
                entry.put("lastMessageTime", latest.get(0).getSentAt());
            }

            if (!entry.isEmpty()) {
                conversations.add(entry);
            }
        }

        // Sort by latest message time descending
        conversations.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("lastMessageTime");
            LocalDateTime timeB = (LocalDateTime) b.get("lastMessageTime");
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        });

        return conversations;
    }

    // NEW: Get display name for a user (firstName for customer, businessName for organizer)
    public String getDisplayName(User user) {
        if (user.getRole().name().equals("CUSTOMER")) {
            return customerProfileRepository.findByUserId(user.getId())
                    .map(CustomerProfile::getFirstName)
                    .orElse(user.getEmail());
        } else {
            return organizerProfileRepository.findByUserId(user.getId())
                    .map(OrganizerProfile::getBusinessName)
                    .orElse(user.getEmail());
        }
    }

    // NEW: Get conversation list for customer inbox
    public List<Map<String, Object>> getConversationListForCustomer(Long customerUserId) {
        List<User> partners = messageRepository.findConversationPartners(customerUserId);

        List<Map<String, Object>> conversations = new ArrayList<>();

        for (User partner : partners) {
            Map<String, Object> entry = new HashMap<>();

            // Get organizer profile for business name
            organizerProfileRepository.findByUserId(partner.getId()).ifPresent(op -> {
                entry.put("organizerBusinessName", op.getBusinessName());
                entry.put("organizerUserId", partner.getId());
            });

            // Get latest message for date/time
            List<Message> latest = messageRepository
                    .findLatestMessageBetweenUsers(customerUserId, partner.getId());
            if (!latest.isEmpty()) {
                entry.put("lastMessageTime", latest.get(0).getSentAt());
            }

            if (!entry.isEmpty()) {
                conversations.add(entry);
            }
        }

        // Sort by latest message time descending
        conversations.sort((a, b) -> {
            LocalDateTime timeA = (LocalDateTime) a.get("lastMessageTime");
            LocalDateTime timeB = (LocalDateTime) b.get("lastMessageTime");
            if (timeA == null) return 1;
            if (timeB == null) return -1;
            return timeB.compareTo(timeA);
        });

        return conversations;
    }
}
