package org.darksamus86.library.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "library.notification";
    public static final String QUEUE_EMAIL = "library.notification.email";
    public static final String ROUTING_KEY_USER_REGISTERED = "user.registered";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(QUEUE_EMAIL, true);
    }

    @Bean
    public Binding emailBinding(DirectExchange notificationExchange, Queue emailQueue) {
        return BindingBuilder
                .bind(emailQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY_USER_REGISTERED);
    }
}
