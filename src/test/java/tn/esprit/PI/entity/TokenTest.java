package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    private Token token;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        token = new Token();
        token.setId(1);
        token.setToken("jwt-token-abc123");
        token.setTokenType(TokenType.BEARER);
        token.setRevoked(false);
        token.setExpired(false);
        token.setUser(testUser);
    }

    @Test
    void testTokenCreation() {
        assertNotNull(token);
        assertEquals(1, token.getId());
        assertEquals("jwt-token-abc123", token.getToken());
        assertEquals(TokenType.BEARER, token.getTokenType());
        assertFalse(token.isRevoked());
        assertFalse(token.isExpired());
    }

    @Test
    void testTokenBuilder() {
        // Arrange & Act
        Token builtToken = Token.builder()
                .id(2)
                .token("built-token")
                .tokenType(TokenType.BEARER)
                .revoked(true)
                .expired(false)
                .user(testUser)
                .build();

        // Assert
        assertNotNull(builtToken);
        assertEquals(2, builtToken.getId());
        assertEquals("built-token", builtToken.getToken());
        assertEquals(TokenType.BEARER, builtToken.getTokenType());
        assertTrue(builtToken.isRevoked());
        assertFalse(builtToken.isExpired());
        assertEquals(testUser, builtToken.getUser());
    }

    @Test
    void testSetRevoked() {
        // Arrange
        token.setRevoked(false);

        // Act
        token.setRevoked(true);

        // Assert
        assertTrue(token.isRevoked());
    }

    @Test
    void testSetExpired() {
        // Arrange
        token.setExpired(false);

        // Act
        token.setExpired(true);

        // Assert
        assertTrue(token.isExpired());
    }

    @Test
    void testUserRelationship() {
        // Assert
        assertNotNull(token.getUser());
        assertEquals(testUser.getId(), token.getUser().getId());
        assertEquals(testUser.getEmail(), token.getUser().getEmail());
    }

    @Test
    void testDefaultTokenType() {
        // Arrange
        Token newToken = new Token();

        // Assert - default value should be BEARER
        assertEquals(TokenType.BEARER, newToken.getTokenType());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Token emptyToken = new Token();

        // Assert
        assertNotNull(emptyToken);
        assertNull(emptyToken.getId());
        assertNull(emptyToken.getToken());
        assertEquals(TokenType.BEARER, emptyToken.getTokenType()); // Default value
        assertFalse(emptyToken.isRevoked());
        assertFalse(emptyToken.isExpired());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        Token fullToken = new Token(3, "full-token", TokenType.BEARER, true, true, testUser);

        // Assert
        assertNotNull(fullToken);
        assertEquals(3, fullToken.getId());
        assertEquals("full-token", fullToken.getToken());
        assertEquals(TokenType.BEARER, fullToken.getTokenType());
        assertTrue(fullToken.isRevoked());
        assertTrue(fullToken.isExpired());
        assertEquals(testUser, fullToken.getUser());
    }

    @Test
    void testSetToken() {
        // Arrange
        String newToken = "new-jwt-token-xyz";

        // Act
        token.setToken(newToken);

        // Assert
        assertEquals(newToken, token.getToken());
    }

    @Test
    void testSetUser() {
        // Arrange
        User newUser = new User();
        newUser.setId(2L);
        newUser.setEmail("newuser@test.com");

        // Act
        token.setUser(newUser);

        // Assert
        assertEquals(newUser, token.getUser());
        assertEquals(2L, token.getUser().getId());
    }

    @Test
    void testTokenEquals() {
        // Arrange
        Token token1 = new Token();
        token1.setId(1);
        token1.setToken("same-token");

        Token token2 = new Token();
        token2.setId(1);
        token2.setToken("same-token");

        // Assert - using Lombok @Data which generates equals()
        assertEquals(token1, token2);
    }

    @Test
    void testTokenHashCode() {
        // Arrange
        Token token1 = new Token();
        token1.setId(1);
        token1.setToken("token");

        Token token2 = new Token();
        token2.setId(1);
        token2.setToken("token");

        // Assert - using Lombok @Data which generates hashCode()
        assertEquals(token1.hashCode(), token2.hashCode());
    }

    @Test
    void testTokenToString() {
        // Act
        String tokenString = token.toString();

        // Assert - should contain key fields (Lombok @Data generates toString())
        assertNotNull(tokenString);
        assertTrue(tokenString.contains("jwt-token-abc123"));
    }

    @Test
    void testBothRevokedAndExpired() {
        // Act
        token.setRevoked(true);
        token.setExpired(true);

        // Assert
        assertTrue(token.isRevoked());
        assertTrue(token.isExpired());
    }

    @Test
    void testNullUser() {
        // Act
        token.setUser(null);

        // Assert
        assertNull(token.getUser());
    }
}
