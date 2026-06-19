package org.darksamus86.library.notification.publisher;

import org.darksamus86.library.config.RabbitMQConfig;
import org.darksamus86.library.notification.event.UserRegisteredEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private UserEventPublisher publisher;

    @Test
    @DisplayName("Should publish UserRegisteredEvent")
    void publishUserRegistered_ShouldSendToRabbitMQ() {
        UserRegisteredEvent event = new UserRegisteredEvent(1L, "test@test.com", "testuser");

        publisher.publishUserRegistered(event);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_USER_REGISTERED,
                event
        );
    }
}
