package com.example.eventplatform.service;

import com.example.eventplatform.entity.Message;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.MessageRepository;
import com.example.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository mockMessageRepository;
    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    private MessageService messageServiceUnderTest;

    @Test
    void testGetMessagesBetweenUsers() {
        // Setup
        // Configure MessageRepository.findMessagesBetweenUsers(...).
        final Message message = new Message();
        final User sender = new User();
        sender.setId(0L);
        message.setSender(sender);
        final User receiver = new User();
        receiver.setId(0L);
        message.setReceiver(receiver);
        message.setContent("content");
        message.setRead(false);
        final List<Message> messages = List.of(message);
        when(mockMessageRepository.findMessagesBetweenUsers(0L, 0L)).thenReturn(messages);

        // Run the test
        final List<Message> result = messageServiceUnderTest.getMessagesBetweenUsers(0L, 0L);

        // Verify the results
    }

    @Test
    void testGetMessagesBetweenUsers_MessageRepositoryReturnsNoItems() {
        // Setup
        when(mockMessageRepository.findMessagesBetweenUsers(0L, 0L)).thenReturn(Collections.emptyList());

        // Run the test
        final List<Message> result = messageServiceUnderTest.getMessagesBetweenUsers(0L, 0L);

        // Verify the results
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testSendMessage() {
        // Setup
        // Configure UserRepository.findById(...).
        final User user1 = new User();
        user1.setId(0L);
        user1.setEmail("email");
        user1.setPasswordHash("passwordHash");
        user1.setRole(UserRole.CUSTOMER);
        user1.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        final Optional<User> user = Optional.of(user1);
        when(mockUserRepository.findById(0L)).thenReturn(user);

        // Configure MessageRepository.save(...).
        final Message message = new Message();
        final User sender = new User();
        sender.setId(0L);
        message.setSender(sender);
        final User receiver = new User();
        receiver.setId(0L);
        message.setReceiver(receiver);
        message.setContent("content");
        message.setRead(false);
        when(mockMessageRepository.save(any(Message.class))).thenReturn(message);

        // Run the test
        final Message result = messageServiceUnderTest.sendMessage(0L, 0L, "content");

        // Verify the results
    }

    @Test
    void testSendMessage_UserRepositoryReturnsAbsent() {
        // Setup
        when(mockUserRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> messageServiceUnderTest.sendMessage(0L, 0L, "content"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void testMarkAsRead() {
        // Setup
        // Configure MessageRepository.findByReceiverIdAndIsReadFalse(...).
        final Message message = new Message();
        final User sender = new User();
        sender.setId(0L);
        message.setSender(sender);
        final User receiver = new User();
        receiver.setId(0L);
        message.setReceiver(receiver);
        message.setContent("content");
        message.setRead(false);
        final List<Message> messages = List.of(message);
        when(mockMessageRepository.findByReceiverIdAndIsReadFalse(0L)).thenReturn(messages);

        // Run the test
        messageServiceUnderTest.markAsRead(0L, 0L);

        // Verify the results
        verify(mockMessageRepository).save(any(Message.class));
    }

    @Test
    void testMarkAsRead_MessageRepositoryFindByReceiverIdAndIsReadFalseReturnsNoItems() {
        // Setup
        when(mockMessageRepository.findByReceiverIdAndIsReadFalse(0L)).thenReturn(Collections.emptyList());

        // Run the test
        messageServiceUnderTest.markAsRead(0L, 0L);

        // Verify the results
    }

    @Test
    void testGetUnreadCount() {
        // Setup
        // Configure MessageRepository.findByReceiverIdAndIsReadFalse(...).
        final Message message = new Message();
        final User sender = new User();
        sender.setId(0L);
        message.setSender(sender);
        final User receiver = new User();
        receiver.setId(0L);
        message.setReceiver(receiver);
        message.setContent("content");
        message.setRead(false);
        final List<Message> messages = List.of(message);
        when(mockMessageRepository.findByReceiverIdAndIsReadFalse(0L)).thenReturn(messages);

        // Run the test
        final int result = messageServiceUnderTest.getUnreadCount(0L);

        // Verify the results
        assertThat(result).isEqualTo(0);
    }

    @Test
    void testGetUnreadCount_MessageRepositoryReturnsNoItems() {
        // Setup
        when(mockMessageRepository.findByReceiverIdAndIsReadFalse(0L)).thenReturn(Collections.emptyList());

        // Run the test
        final int result = messageServiceUnderTest.getUnreadCount(0L);

        // Verify the results
        assertThat(result).isEqualTo(0);
    }
}
