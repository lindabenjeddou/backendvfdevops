package tn.esprit.PI.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.repository.TokenRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private HandlerExceptionResolver handlerExceptionResolver;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String token = "valid-token";
        String email = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);

        UserDetails userDetails = new User(
                email,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        Token storedToken = Token.builder()
                .token(token)
                .expired(false)
                .revoked(false)
                .build();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(storedToken));
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
        assertThat(authentication.isAuthenticated()).isTrue();

        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(tokenRepository).findByToken(token);
        verify(jwtService).isTokenValid(token, userDetails);
        verifyNoMoreInteractions(handlerExceptionResolver);
    }

    @Test
    void doFilterInternal_invalidOrNoUsername_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid-token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(jwtService).extractUsername(token);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService, tokenRepository, handlerExceptionResolver);
    }

    @Test
    void doFilterInternal_revokedOrExpiredToken_doesNotSetAuthentication() throws ServletException, IOException {
        String token = "revoked-or-expired-token";
        String email = "user@example.com";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);

        UserDetails userDetails = new User(
                email,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        Token storedToken = Token.builder()
                .token(token)
                .expired(true)   // ou revoked = true, l'un des deux suffit à l'invalider
                .revoked(false)
                .build();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(storedToken));

        // Le JWT en lui-même pourrait être techniquement valide,
        // mais l'état en base (expired/revoked) doit empêcher l'authentification.
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(request).getHeader("Authorization");
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(email);
        verify(tokenRepository).findByToken(token);
        verify(jwtService).isTokenValid(token, userDetails);
        verify(filterChain).doFilter(request, response);
        verifyNoMoreInteractions(handlerExceptionResolver);
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_callsNextFilterOnly() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository, handlerExceptionResolver);
    }
}
