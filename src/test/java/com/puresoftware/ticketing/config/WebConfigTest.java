package com.puresoftware.ticketing.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

class WebConfigTest {
    @Test
    void addCorsMappings_executes() {
        WebConfig cfg = new WebConfig();
        cfg.addCorsMappings(new CorsRegistry());
    }
}
