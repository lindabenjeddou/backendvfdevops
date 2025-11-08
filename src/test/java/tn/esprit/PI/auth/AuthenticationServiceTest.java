package tn.esprit.PI.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.PI.config.JwtService;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.entity.User;
import tn.esprit.PI.entity.UserRole;
import tn.esprit.PI.repository.TokenRepository;
import tn.esprit.PI.repository.UserRepository;
import tn.esprit.PI.util.NotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private RegisterRequest registerRequest;
    private AuthenticationRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.CHEF_PROJET)
                .confirmation(1)
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setFirstname("New");
        registerRequest.setLastname("User");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");

        authRequest = new AuthenticationRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");
    }

    @Test
    void testRegister_Success() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(repository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        // Act
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertNull(response.getError());
        verify(repository, times(1)).save(any(User.class));
        verify(tokenRepository, times(1)).save(any(Token.class));
    }

    @Test
    void testLogin_Success() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(any(), anyLong(), anyString())).thenReturn("jwt-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
        when(tokenRepository.save(any(Token.class))).thenReturn(new Token());

        // Act
        AuthenticationResponse response = authenticationService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(1L, response.getUserId());
        assertEquals("CHEF_PROJET", response.getRole());
        assertEquals("test@example.com", response.getEmail());
        assertNull(response.getError());
        verify(repository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            authenticationService.login(authRequest);
        });
    }

    @Test
    void testLogin_UserNotConfirmed() {
        // Arrange
        testUser.setConfirmation(0); // Not confirmed
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        AuthenticationResponse response = authenticationService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertNull(response.getAccessToken());
        assertNotNull(response.getError());
        assertTrue(response.getError().contains("not confirmed"));
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act
        AuthenticationResponse response = authenticationService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertNull(response.getAccessToken());
        assertNotNull(response.getError());
        assertEquals("Invalid credentials", response.getError());
    }

    @Test
    void testUpdatePassword_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(repository.save(any(User.class))).thenReturn(testUser);

        // Act
        authenticationService.updatePassword(1L, "oldPassword", "newPassword");

        // Assert
        verify(repository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        // Arrange
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            authenticationService.updatePassword(999L, "oldPassword", "newPassword");
        });
    }

    @Test
    void testUpdatePassword_IncorrectCurrentPassword() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.updatePassword(1L, "wrongPassword", "newPassword");
        });
        assertTrue(exception.getMessage().contains("Current password is incorrect"));
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Arrange
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer refresh-token");
        when(jwtService.extractUsername("refresh-token")).thenReturn("test@example.com");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(anyString(), any())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("new-jwt-token");
        when(tokenRepository.findAllValidTokenByUser(1L)).thenReturn(Collections.emptyList());
        when(response.getOutputStream()).thenReturn(mock(jakarta.servlet.ServletOutputStream.class));

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService, times(1)).extractUsername("refresh-token");
        verify(jwtService, times(1)).generateToken(any());
    }

    @Test
    void testRefreshToken_NoAuthHeader() throws Exception {
        // Arrange
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Arrange
        jakarta.servlet.http.HttpServletRequest request = mock(jakarta.servlet.http.HttpServletRequest.class);
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtService.extractUsername("invalid-token")).thenReturn("test@example.com");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.isTokenValid(anyString(), any())).thenReturn(false);

        // Act
        authenticationService.refreshToken(request, response);

        // Assert
        verify(jwtService, never()).generateToken(any());
    }
}
