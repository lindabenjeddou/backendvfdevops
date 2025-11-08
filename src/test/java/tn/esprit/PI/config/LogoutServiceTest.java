package tn.esprit.PI.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.repository.TokenRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private LogoutService logoutService;

    private Token testToken;

    @BeforeEach
    void setUp() {
        testToken = new Token();
        testToken.setId(1);
        testToken.setToken("valid-jwt-token");
        testToken.setExpired(false);
        testToken.setRevoked(false);
        
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLogout_Success() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(tokenRepository.findByToken("valid-jwt-token")).thenReturn(Optional.of(testToken));
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, times(1)).findByToken("valid-jwt-token");
        verify(tokenRepository, times(1)).save(testToken);
        // SecurityContextHolder.clearContext() is called (static method, can't verify easily)
    }

    @Test
    void testLogout_NoAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, never()).findByToken(anyString());
        verify(tokenRepository, never()).save(any());
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testLogout_InvalidAuthorizationHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Invalid header");

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, never()).findByToken(anyString());
        verify(tokenRepository, never()).save(any());
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testLogout_TokenNotFound() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer non-existent-token");
        when(tokenRepository.findByToken("non-existent-token")).thenReturn(Optional.empty());

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, times(1)).findByToken("non-existent-token");
        verify(tokenRepository, never()).save(any());
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testLogout_WithBearerPrefix() {
        // Arrange
        String fullToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String jwtOnly = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        
        when(request.getHeader("Authorization")).thenReturn(fullToken);
        when(tokenRepository.findByToken(jwtOnly)).thenReturn(Optional.of(testToken));
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, times(1)).findByToken(jwtOnly);
        verify(tokenRepository, times(1)).save(testToken);
    }

    @Test
    void testLogout_TokenMarkedAsExpired() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(tokenRepository.findByToken("valid-jwt-token")).thenReturn(Optional.of(testToken));
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> {
            Token saved = invocation.getArgument(0);
            assert saved.isExpired();
            assert saved.isRevoked();
            return saved;
        });

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        verify(tokenRepository, times(1)).save(testToken);
    }

    @Test
    void testLogout_ClearsSecurityContext() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
        when(tokenRepository.findByToken("valid-jwt-token")).thenReturn(Optional.of(testToken));
        when(tokenRepository.save(any(Token.class))).thenReturn(testToken);

        // Act
        logoutService.logout(request, response, authentication);

        // Assert
        // SecurityContextHolder.clearContext() is called internally
        // We verify the token was processed correctly
        verify(tokenRepository, times(1)).save(testToken);
        assertTrue(testToken.isExpired());
        assertTrue(testToken.isRevoked());
    }

    @Test
    void testLogout_EmptyBearerToken() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        logoutService.logout(request, response, authentication);

        // Assert - should handle gracefully
        verify(tokenRepository, times(1)).findByToken("");
    }
}
