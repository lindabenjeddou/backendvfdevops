package tn.esprit.PI.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import tn.esprit.PI.entity.Token;              // ✅ TON VRAI PACKAGE
import tn.esprit.PI.repository.TokenRepository;

import java.io.IOException;
import java.util.Optional;

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
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    void should_set_authentication_when_token_valid() throws ServletException, IOException {
        String jwt = "validToken";
        String email = "user@test.com";

        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(email);

        UserDetails userDetails = User
                .withUsername(email)
                .password("pwd")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        Token storedToken = new Token();
        storedToken.setExpired(false);
        storedToken.setRevoked(false);
        when(tokenRepository.findByToken(jwt)).thenReturn(Optional.of(storedToken));

        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken;

        SecurityContextHolder.clearContext();
    }

    @Test
    void should_skip_when_no_auth_header() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/secure");
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }

    @Test
    void should_skip_for_public_endpoints() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/actuator/health");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assert SecurityContextHolder.getContext().getAuthentication() == null;
    }
}
