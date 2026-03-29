package com.example.eventplatform.service;

import com.example.eventplatform.entity.Booking;
import com.example.eventplatform.entity.Message;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.repository.BookingRepository;
import com.example.eventplatform.repository.MessageRepository;
import com.example.eventplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all messages between two users (both directions)
    public List<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        return messageRepository.findMessagesBetweenUsers(userId1, userId2);
    }

    // Send a message
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

    // Mark messages as read for the current viewer
    // currentUserId = person currently viewing (receiver of incoming messages)
    // otherUserId   = person who sent the messages
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

    // Get unread message count
    public int getUnreadCount(Long receiverId) {
        return messageRepository
                .findByReceiverIdAndIsReadFalse(receiverId)
                .size();
    }
}
