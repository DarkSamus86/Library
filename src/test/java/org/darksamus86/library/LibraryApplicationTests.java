package org.darksamus86.library;

import org.darksamus86.library.config.RedisHealthCheck;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class LibraryApplicationTests {

    @MockitoBean
    private RedisHealthCheck redisHealthCheck;

    @Test
    void contextLoads() {
    }

}
