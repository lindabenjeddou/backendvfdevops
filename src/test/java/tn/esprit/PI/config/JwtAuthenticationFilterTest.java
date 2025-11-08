package tn.esprit.PI.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import tn.esprit.PI.entity.Token;
import tn.esprit.PI.repository.TokenRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires ciblés pour augmenter la couverture sur JwtAuthenticationFilter.
 */
class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsService userDetailsService;
    private TokenRepository tokenRepository;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        userDetailsService = mock(UserDetailsService.class);
        tokenRepository = mock(TokenRepository.class);

        filter = new JwtAuthenticationFilter(jwtService, userDetailsService, tokenRepository);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipExcludedPaths() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/actuator/health");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    @Test
    void shouldNotAuthenticateWhenNoAuthorizationHeader() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldAuthenticateWhenJwtIsValidAndNotRevoked() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt");

        UserDetails userDetails =
                new User("user@test.com", "password", Collections.emptyList());

        Token token = new Token();
        token.setToken("valid-jwt");
        token.setExpired(false);
        token.setRevoked(false);

        when(jwtService.extractUsername("valid-jwt")).thenReturn("user@test.com");
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(tokenRepository.findByToken("valid-jwt")).thenReturn(Optional.of(token));
        when(jwtService.isTokenValid("valid-jwt", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(userDetails);
    }

    @Test
    void shouldNotAuthenticateWhenTokenExpiredOrInvalid() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer bad-jwt");

        UserDetails userDetails =
                new User("user@test.com", "password", Collections.emptyList());

        Token token = new Token();
        token.setToken("bad-jwt");
        token.setExpired(true);      // simul token expiré
        token.setRevoked(false);

        when(jwtService.extractUsername("bad-jwt")).thenReturn("user@test.com");
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(tokenRepository.findByToken("bad-jwt")).thenReturn(Optional.of(token));
        when(jwtService.isTokenValid("bad-jwt", userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
