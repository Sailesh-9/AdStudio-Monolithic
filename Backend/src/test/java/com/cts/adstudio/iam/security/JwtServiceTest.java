package com.cts.adstudio.iam.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure unit tests for {@link JwtService} - no Spring context, no database. The
 * {@code @Value}-injected fields are populated directly via reflection. The secret
 * must be at least 256 bits for HMAC-SHA256.
 */
class JwtServiceTest {

    private static final String SECRET = "test-secret-key-test-secret-key-0123456789-abcdef";
    private static final String EMAIL = "admin@adstudio.com";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86_400_000L);
    }

    @Test
    void generatedToken_carriesSubjectAndRole_andValidates() {
        String token = jwtService.generateToken(EMAIL, "ADMIN", 1L);

        assertEquals(EMAIL, jwtService.extractUsername(token));
        assertEquals("ADMIN", jwtService.extractClaim(token, claims -> claims.get("role", String.class)));
        assertTrue(jwtService.isTokenValid(token, EMAIL));
    }

    @Test
    void isTokenValid_falseWhenUsernameDoesNotMatch() {
        String token = jwtService.generateToken(EMAIL, "ADMIN", 1L);

        assertFalse(jwtService.isTokenValid(token, "someone-else@adstudio.com"));
    }

    @Test
    void expiredToken_isRejected() {
        // expiry in the past -> JJWT raises ExpiredJwtException on parse/validation
        ReflectionTestUtils.setField(jwtService, "expirationMs", -60_000L);
        String expired = jwtService.generateToken(EMAIL, "ADMIN", 1L);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expired, EMAIL));
    }

    @Test
    void malformedToken_isRejected() {
        assertThrows(RuntimeException.class, () -> jwtService.extractUsername("not-a-real-token"));
    }
}
