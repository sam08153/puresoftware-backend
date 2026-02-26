package com.puresoftware.ticketing.config;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class RedisConfigTest {
    @Test
    void beanCreation_returnsTemplate() {
        RedisConfig cfg = new RedisConfig();
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        StringRedisTemplate tpl = cfg.stringRedisTemplate(factory);
        assertNotNull(tpl);
    }
}
