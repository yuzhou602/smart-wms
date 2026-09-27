package com.smartwms.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private static final String TEST_SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void generatedTokenContainsExpectedIdentity() {
        JwtTokenProvider provider = new JwtTokenProvider(TEST_SECRET, 60_000);

        String token = provider.generateToken(42L, "warehouse_manager");
        Claims claims = provider.parseToken(token);

        assertEquals(42L, claims.get("userId", Long.class));
        assertEquals("warehouse_manager", claims.getSubject());
        assertTrue(provider.validateToken(token));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtTokenProvider provider = new JwtTokenProvider(TEST_SECRET, 60_000);
        String token = provider.generateToken(42L, "warehouse_manager");

        assertFalse(provider.validateToken(token + "tampered"));
    }

    @Test
    void expiredTokenIsReportedAsExpired() {
        JwtTokenProvider provider = new JwtTokenProvider(TEST_SECRET, -1_000);
        String token = provider.generateToken(42L, "warehouse_manager");

        assertTrue(provider.isTokenExpired(token));
        assertFalse(provider.validateToken(token));
    }
}
