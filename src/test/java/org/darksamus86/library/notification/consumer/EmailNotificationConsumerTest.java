package org.darksamus86.library.notification.consumer;

import org.darksamus86.library.notification.event.UserRegisteredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationConsumerTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailNotificationConsumer consumer;

    @Test
    @DisplayName("Should send welcome email")
    void handleUserRegistered_ShouldSendEmail() {
        UserRegisteredEvent event = new UserRegisteredEvent(1L, "test@test.com", "testuser");

        consumer.handleUserRegistered(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle email sending failure gracefully")
    void handleUserRegistered_WhenMailFails_ShouldNotThrow() {
        UserRegisteredEvent event = new UserRegisteredEvent(1L, "test@test.com", "testuser");

        doThrow(new MailException("Mail server down") {}).when(mailSender).send(any(SimpleMailMessage.class));

        consumer.handleUserRegistered(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
