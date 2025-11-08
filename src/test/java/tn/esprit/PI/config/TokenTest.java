package tn.esprit.PI.config;

import org.junit.jupiter.api.Test;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.entity.TokenType;
import tn.esprit.PI.entity.User;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @Test
    void builderShouldSetFieldsCorrectly() {
        User user = new User();

        Token token = Token.builder()
                .id(1)
                .token("abc123")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build();

        assertThat(token.getId()).isEqualTo(1);
        assertThat(token.getToken()).isEqualTo("abc123");
        assertThat(token.getTokenType()).isEqualTo(TokenType.BEARER);
        assertThat(token.isExpired()).isFalse();
        assertThat(token.isRevoked()).isFalse();
        assertThat(token.getUser()).isSameAs(user);
    }
}
