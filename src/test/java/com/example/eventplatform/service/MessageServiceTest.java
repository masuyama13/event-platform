package com.example.eventplatform.service;

import com.example.eventplatform.entity.Message;
import com.example.eventplatform.entity.CustomerProfile;
import com.example.eventplatform.entity.OrganizerProfile;
import com.example.eventplatform.entity.User;
import com.example.eventplatform.entity.UserRole;
import com.example.eventplatform.repository.CustomerProfileRepository;
import com.example.eventplatform.repository.MessageRepository;
import com.example.eventplatform.repository.OrganizerProfileRepository;
import com.example.eventplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
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
    @Mock
    private CustomerProfileRepository mockCustomerProfileRepository;
    @Mock
    private OrganizerProfileRepository mockOrganizerProfileRepository;

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
        assertThat(result).isEqualTo(1);
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

    @Test
    void getDisplayName_shouldUseCustomerFirstName() {
        User customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@test.com");
        customer.setRole(UserRole.CUSTOMER);

        CustomerProfile profile = new CustomerProfile();
        profile.setFirstName("John");
        when(mockCustomerProfileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        String result = messageServiceUnderTest.getDisplayName(customer);

        assertThat(result).isEqualTo("John");
    }

    @Test
    void getConversationListForOrganizer_shouldSortByLatestMessageDescending() {
        User customer1 = new User();
        customer1.setId(10L);
        customer1.setRole(UserRole.CUSTOMER);
        User customer2 = new User();
        customer2.setId(20L);
        customer2.setRole(UserRole.CUSTOMER);

        CustomerProfile profile1 = new CustomerProfile();
        profile1.setFirstName("Alice");
        CustomerProfile profile2 = new CustomerProfile();
        profile2.setFirstName("Bob");

        Message latestForCustomer1 = new Message();
        latestForCustomer1.setSentAt(LocalDateTime.of(2030, 1, 3, 10, 0));
        Message latestForCustomer2 = new Message();
        latestForCustomer2.setSentAt(LocalDateTime.of(2030, 1, 4, 10, 0));

        when(mockMessageRepository.findConversationPartners(1L)).thenReturn(List.of(customer1, customer2));
        when(mockCustomerProfileRepository.findByUserId(10L)).thenReturn(Optional.of(profile1));
        when(mockCustomerProfileRepository.findByUserId(20L)).thenReturn(Optional.of(profile2));
        when(mockMessageRepository.findLatestMessageBetweenUsers(1L, 10L)).thenReturn(List.of(latestForCustomer1));
        when(mockMessageRepository.findLatestMessageBetweenUsers(1L, 20L)).thenReturn(List.of(latestForCustomer2));

        List<Map<String, Object>> result = messageServiceUnderTest.getConversationListForOrganizer(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).get("customerFirstName")).isEqualTo("Bob");
        assertThat(result.get(1).get("customerFirstName")).isEqualTo("Alice");
    }

    @Test
    void getConversationListForCustomer_shouldIncludeOrganizerBusinessName() {
        User organizer = new User();
        organizer.setId(30L);
        organizer.setRole(UserRole.ORGANIZER);

        OrganizerProfile organizerProfile = new OrganizerProfile();
        organizerProfile.setBusinessName("Aurora Events");

        Message latestMessage = new Message();
        latestMessage.setSentAt(LocalDateTime.of(2030, 1, 5, 12, 0));

        when(mockMessageRepository.findConversationPartners(2L)).thenReturn(List.of(organizer));
        when(mockOrganizerProfileRepository.findByUserId(30L)).thenReturn(Optional.of(organizerProfile));
        when(mockMessageRepository.findLatestMessageBetweenUsers(2L, 30L)).thenReturn(List.of(latestMessage));

        List<Map<String, Object>> result = messageServiceUnderTest.getConversationListForCustomer(2L);

        assertThat(result).singleElement().satisfies(entry -> {
            assertThat(entry.get("organizerBusinessName")).isEqualTo("Aurora Events");
            assertThat(entry.get("organizerUserId")).isEqualTo(30L);
            assertThat(entry.get("lastMessageTime")).isEqualTo(LocalDateTime.of(2030, 1, 5, 12, 0));
        });
    }
}
