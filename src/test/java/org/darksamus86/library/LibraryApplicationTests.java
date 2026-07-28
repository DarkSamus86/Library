package org.darksamus86.library;

import org.darksamus86.library.config.RedisHealthCheck;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.rabbitmq.listener.simple.auto-startup=false")
class LibraryApplicationTests {

    @MockitoBean
    private RedisHealthCheck redisHealthCheck;

    @Test
    void contextLoads() {
    }

}
