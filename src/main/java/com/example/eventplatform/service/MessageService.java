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
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all messages for a booking
    public List<Message> getMessagesByBooking(Long bookingId) {
        return messageRepository.findByBookingIdOrderBySentAtAsc(bookingId);
    }

    // Send a message
    public Message sendMessage(Long bookingId, Long senderId,
                               Long receiverId, String content) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message();
        message.setBooking(booking);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        return messageRepository.save(message);
    }

    // Mark messages as read
    public void markAsRead(Long bookingId, Long receiverId) {
        List<Message> unreadMessages = messageRepository
                .findByReceiverIdAndIsReadFalse(receiverId);

        unreadMessages.forEach(message -> {
            if (message.getBooking().getId().equals(bookingId)) {
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
