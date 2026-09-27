package com.smartwms.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityConfigTest {

    @Test
    void corsOriginsAreReadFromConfigurationAndTrimmed() {
        SecurityConfig securityConfig = new SecurityConfig(null);
        ReflectionTestUtils.setField(
                securityConfig,
                "allowedOrigins",
                "https://wms.example.com, https://ops.example.com"
        );

        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/inventory");
        CorsConfiguration configuration = source.getCorsConfiguration(request);

        assertNotNull(configuration);
        assertEquals(
                List.of("https://wms.example.com", "https://ops.example.com"),
                configuration.getAllowedOrigins()
        );
    }

    @Test
    void emptyCorsSettingDoesNotAllowCrossOriginRequests() {
        SecurityConfig securityConfig = new SecurityConfig(null);
        ReflectionTestUtils.setField(securityConfig, "allowedOrigins", "");

        CorsConfiguration configuration = securityConfig.corsConfigurationSource()
                .getCorsConfiguration(new MockHttpServletRequest("GET", "/inventory"));

        assertNotNull(configuration);
        assertEquals(List.of(), configuration.getAllowedOrigins());
    }
}
