package com.example.eventplatform.repository;

import com.example.eventplatform.entity.Message;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findMessagesBetweenUsers_shouldReturnConversationInAscendingOrder() {
        User customer = saveUser("customer@test.com", UserRole.CUSTOMER);
        User organizer = saveUser("organizer@test.com", UserRole.ORGANIZER);
        User outsider = saveUser("other@test.com", UserRole.CUSTOMER);

        Message first = saveMessage(customer, organizer, "first", 1);
        Message second = saveMessage(organizer, customer, "second", 2);
        saveMessage(outsider, organizer, "ignore me", 3);

        List<Message> messages = messageRepository.findMessagesBetweenUsers(customer.getId(), organizer.getId());

        assertThat(messages).extracting(Message::getId).containsExactly(first.getId(), second.getId());
    }

    @Test
    void findByReceiverIdAndIsReadFalse_shouldReturnOnlyUnreadMessagesForReceiver() {
        User customer = saveUser("customer@test.com", UserRole.CUSTOMER);
        User organizer = saveUser("organizer@test.com", UserRole.ORGANIZER);
        User outsider = saveUser("other@test.com", UserRole.CUSTOMER);

        Message unread = new Message();
        unread.setSender(customer);
        unread.setReceiver(organizer);
        unread.setContent("new message");
        unread.setRead(false);
        unread = messageRepository.save(unread);

        Message otherReceiverMessage = new Message();
        otherReceiverMessage.setSender(customer);
        otherReceiverMessage.setReceiver(outsider);
        otherReceiverMessage.setContent("different inbox");
        otherReceiverMessage.setRead(false);
        messageRepository.save(otherReceiverMessage);

        List<Message> messages = messageRepository.findByReceiverIdAndIsReadFalse(organizer.getId());

        assertThat(messages).extracting(Message::getId).containsExactly(unread.getId());
        assertThat(messages).extracting(Message::isRead).containsExactly(false);
    }

    private User saveUser(String email, UserRole role) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("secret");
        user.setRole(role);
        return userRepository.save(user);
    }

    private Message saveMessage(User sender, User receiver, String content, int hour) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setRead(false);
        message.setSentAt(LocalDateTime.of(2030, 1, 1, hour, 0));
        return messageRepository.save(message);
    }
}
