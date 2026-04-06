package com.example.eventplatform.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    @Test
    void onCreate_shouldSetSentAtAndUnreadByDefault() {
        Message message = new Message();
        message.setRead(true);

        message.onCreate();

        assertThat(message.getSentAt()).isNotNull();
        assertThat(message.isRead()).isFalse();
    }
}
