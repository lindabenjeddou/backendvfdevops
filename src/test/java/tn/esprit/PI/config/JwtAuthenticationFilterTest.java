package tn.esprit.PI.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import tn.esprit.PI.entity.Token;
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

    @Mock
    private UserDetails userDetails;

    @Mock
    private Token token;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Les paths exclus (ex: /actuator/**) doivent être ignorés par le filtre.
     */
    @Test
    void doFilterInternal_shouldSkipExcludedPaths() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/actuator/health");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    /**
     * Pas de header Authorization -> on laisse passer sans toucher au SecurityContext.
     */
    @Test
    void doFilterInternal_noAuthorizationHeader_callsNextFilter() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/PI/secure");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    /**
     * Header qui ne commence pas par Bearer -> ignoré.
     */
    @Test
    void doFilterInternal_invalidAuthorizationHeader_callsNextFilter() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/PI/secure");
        when(request.getHeader("Authorization")).thenReturn("Basic xxx");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, tokenRepository);
    }

    /**
     * Token valide + non expiré/non révoqué -> authentification doit être créée.
     */
    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String jwt = "valid.jwt.token";
        String email = "user@test.com";

        when(request.getServletPath()).thenReturn("/PI/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        when(tokenRepository.findByToken(jwt)).thenReturn(Optional.of(token));
        when(token.isExpired()).thenReturn(false);
        when(token.isRevoked()).thenReturn(false);

        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // la chaîne continue
        verify(filterChain, times(1)).doFilter(request, response);

        // et une Authentication est bien positionnée
        assert SecurityContextHolder.getContext().getAuthentication() != null;
        verify(userDetailsService, times(1)).loadUserByUsername(email);
    }

    /**
     * Token trouvé mais marqué expiré/révoqué -> NE DOIT PAS authentifier l'utilisateur.
     * Couvre la branche souvent manquante pour Sonar (revoked/expired).
     */
    @Test
    void doFilterInternal_revokedOrExpiredToken_doesNotSetAuthentication() throws ServletException, IOException {
        String jwt = "bad.jwt.token";
        String email = "user@test.com";

        when(request.getServletPath()).thenReturn("/PI/secure");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Token trouvé en BDD mais invalide
        when(tokenRepository.findByToken(jwt)).thenReturn(Optional.of(token));
        when(token.isExpired()).thenReturn(true);
        when(token.isRevoked()).thenReturn(true);

        // Même si la signature est "valide", les flags BDD doivent l'emporter
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // la chaîne continue
        verify(filterChain, times(1)).doFilter(request, response);

        // mais aucun utilisateur ne doit être authentifié
        assert SecurityContextHolder.getContext().getAuthentication() == null;
        // pas besoin d'autres interactions
    }
}
