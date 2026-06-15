package org.darksamus86.library.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // NOTIFICATION

    public static final String NOTIFICATION_EXCHANGE = "library.notification";
    public static final String QUEUE_EMAIL = "library.notification.email";
    public static final String ROUTING_KEY_USER_REGISTERED = "user.registered";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
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

    // BOOK IMPORT

    public static final String BOOK_EXCHANGE = "library.book";
    public static final String QUEUE_BOOK_IMPORT = "library.book.import";
    public static final String ROUTING_KEY_BOOK_IMPORT = "book.import";
    public static final String DLQ_BOOK_IMPORT = "library.book.import.dlq";

    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange(BOOK_EXCHANGE);
    }

    @Bean
    public Queue bookImportQueue() {
        return QueueBuilder.durable(QUEUE_BOOK_IMPORT)
                .withArgument("x-dead-letter-exchange", BOOK_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "book.import.dlq")
                .build();
    }

    @Bean
    public Binding bookImportBinding(TopicExchange bookExchange, Queue bookImportQueue) {
        return BindingBuilder
                .bind(bookImportQueue)
                .to(bookExchange)
                .with(ROUTING_KEY_BOOK_IMPORT);
    }

    @Bean
    public Queue bookImportDlq() {
        return QueueBuilder.durable(DLQ_BOOK_IMPORT).build();
    }

    @Bean
    public Binding bookImportDlqBinding(TopicExchange bookExchange, Queue bookImportDlq) {
        return BindingBuilder
                .bind(bookImportDlq)
                .to(bookExchange)
                .with("book.import.dlq");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
