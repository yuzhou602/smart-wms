package com.smartwms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("prod")
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:prodtest;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.autoconfigure.exclude=org.redisson.spring.starter.RedissonAutoConfigurationV2,"
                + "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration",
        "jwt.secret=0123456789abcdef0123456789abcdef"
})
class ProductionProfileTests {

    @Autowired
    private Environment environment;

    @Test
    void productionDefaultsDisableOptionalPublicEndpointsAndAi() {
        assertEquals("false", environment.getProperty("springdoc.api-docs.enabled"));
        assertEquals("false", environment.getProperty("springdoc.swagger-ui.enabled"));
        assertEquals("none", environment.getProperty("spring.ai.model.chat"));
        assertEquals("none", environment.getProperty("spring.ai.model.embedding"));
        assertEquals("none", environment.getProperty("spring.ai.model.image"));
        assertEquals("none", environment.getProperty("spring.ai.model.audio.speech"));
        assertEquals("none", environment.getProperty("spring.ai.model.audio.transcription"));
        assertEquals("none", environment.getProperty("spring.ai.model.moderation"));
        assertEquals("", environment.getProperty("app.cors.allowed-origins"));
    }
}
