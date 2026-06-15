package org.darksamus86.library.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.darksamus86.library.config.RabbitMQConfig;
import org.darksamus86.library.notification.event.UserRegisteredEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationConsumer {

    private final JavaMailSender mailSender;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_EMAIL)
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: userId={}, email={}", event.userId(), event.email());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.email());
            message.setSubject("Welcome to the Library!");
            message.setText("Hello " + event.username() + ",\n\n"
                    + "Thank you for registering at Library. Your account has been created successfully.\n\n"
                    + "Best regards,\nLibrary Team");

            mailSender.send(message);
            log.info("Welcome email sent to: {}", event.email());
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", event.email(), e.getMessage());
        }
    }
}
