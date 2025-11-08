package tn.esprit.PI.config;

import org.junit.jupiter.api.Test;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.entity.TokenType;
import tn.esprit.PI.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void builder_shouldSetAllFieldsCorrectly() {
        User user = new User();
        user.setId(1L);

        Token token = Token.builder()
                .id(1)
                .token("sample-jwt-token")
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .user(user)
                .build();

        assertEquals(1, token.getId());
        assertEquals("sample-jwt-token", token.getToken());
        assertEquals(TokenType.BEARER, token.getTokenType());
        assertFalse(token.isRevoked());
        assertFalse(token.isExpired());
        assertEquals(user, token.getUser());
    }

    @Test
    void defaultTokenType_shouldBeBearer() {
        Token token = new Token();
        assertEquals(TokenType.BEARER, token.getTokenType());
    }
}
