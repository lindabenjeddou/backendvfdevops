package tn.esprit.PI.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("password123");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setRole(UserRole.ADMIN);
        user.setConfirmation(1);
        user.setStatus(1);
    }

    @Test
    void testIsResetTokenValid_ValidToken() {
        // Arrange
        user.setResetToken("valid-token");
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));

        // Act & Assert
        assertTrue(user.isResetTokenValid());
    }

    @Test
    void testIsResetTokenValid_ExpiredToken() {
        // Arrange
        user.setResetToken("expired-token");
        user.setResetTokenExpiration(LocalDateTime.now().minusHours(1));

        // Act & Assert
        assertFalse(user.isResetTokenValid());
    }

    @Test
    void testIsResetTokenValid_NullToken() {
        // Arrange
        user.setResetToken(null);
        user.setResetTokenExpiration(LocalDateTime.now().plusHours(1));

        // Act & Assert
        assertFalse(user.isResetTokenValid());
    }

    @Test
    void testIsResetTokenValid_NullExpiration() {
        // Arrange
        user.setResetToken("token");
        user.setResetTokenExpiration(null);

        // Act & Assert
        assertFalse(user.isResetTokenValid());
    }

    @Test
    void testIsResetTokenValid_BothNull() {
        // Arrange
        user.setResetToken(null);
        user.setResetTokenExpiration(null);

        // Act & Assert
        assertFalse(user.isResetTokenValid());
    }

    @Test
    void testGetUsername_ReturnsEmail() {
        // Act
        String username = user.getUsername();

        // Assert
        assertEquals("test@test.com", username);
    }

    @Test
    void testGetPassword() {
        // Act
        String password = user.getPassword();

        // Assert
        assertEquals("password123", password);
    }

    @Test
    void testIsAccountNonExpired_ReturnsTrue() {
        // Act & Assert
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked_ReturnsTrue() {
        // Act & Assert
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired_ReturnsTrue() {
        // Act & Assert
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled_ReturnsTrue() {
        // Act & Assert
        assertTrue(user.isEnabled());
    }

    @Test
    void testGetAuthorities_ReturnsNull() {
        // Act & Assert
        assertNull(user.getAuthorities());
    }

    @Test
    void testBuilder() {
        // Arrange & Act
        User builtUser = User.builder()
                .id(2L)
                .email("builder@test.com")
                .firstname("Jane")
                .lastname("Smith")
                .password("pass456")
                .role(UserRole.MAGASINIER)
                .confirmation(1)
                .build();

        // Assert
        assertNotNull(builtUser);
        assertEquals(2L, builtUser.getId());
        assertEquals("builder@test.com", builtUser.getEmail());
        assertEquals("Jane", builtUser.getFirstname());
        assertEquals("Smith", builtUser.getLastname());
        assertEquals(UserRole.MAGASINIER, builtUser.getRole());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        User newUser = new User();
        
        // Act
        newUser.setId(3L);
        newUser.setEmail("setters@test.com");
        newUser.setFirstname("Test");
        newUser.setLastname("User");
        newUser.setPassword("testpass");
        newUser.setPhoneNumber("123456789");
        newUser.setAdress("123 Test St");
        newUser.setRole(UserRole.CHEF_PROJET);
        newUser.setConfirmation(1);
        newUser.setStatus(1);
        newUser.setToken("jwt-token");
        newUser.setResetToken("reset-token");
        newUser.setResetTokenExpiration(LocalDateTime.now().plusDays(1));

        // Assert
        assertEquals(3L, newUser.getId());
        assertEquals("setters@test.com", newUser.getEmail());
        assertEquals("Test", newUser.getFirstname());
        assertEquals("User", newUser.getLastname());
        assertEquals("testpass", newUser.getPassword());
        assertEquals("123456789", newUser.getPhoneNumber());
        assertEquals("123 Test St", newUser.getAdress());
        assertEquals(UserRole.CHEF_PROJET, newUser.getRole());
        assertEquals(1, newUser.getConfirmation());
        assertEquals(1, newUser.getStatus());
        assertEquals("jwt-token", newUser.getToken());
        assertEquals("reset-token", newUser.getResetToken());
        assertNotNull(newUser.getResetTokenExpiration());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        User emptyUser = new User();

        // Assert
        assertNotNull(emptyUser);
        assertNull(emptyUser.getId());
        assertNull(emptyUser.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange & Act
        User fullUser = new User(
                4L,
                "first",
                "last",
                "email@test.com",
                "password",
                "token",
                UserRole.TECHNICIEN_CURATIF,
                new ArrayList<>(),
                "reset-token",
                LocalDateTime.now(),
                "555-1234",
                "Address",
                1,
                1,
                new ArrayList<>(),
                new ArrayList<>()
        );

        // Assert
        assertNotNull(fullUser);
        assertEquals(4L, fullUser.getId());
        assertEquals("email@test.com", fullUser.getEmail());
        assertEquals(UserRole.TECHNICIEN_CURATIF, fullUser.getRole());
    }

    @Test
    void testTokensRelationship() {
        // Arrange
        Token token = new Token();
        token.setId(1);
        token.setToken("jwt-token");
        
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(token);
        
        // Act
        user.setTokens(tokens);

        // Assert
        assertNotNull(user.getTokens());
        assertEquals(1, user.getTokens().size());
        assertEquals("jwt-token", user.getTokens().get(0).getToken());
    }

    @Test
    void testPlanningHorairesRelationship() {
        // Arrange
        PlanningHoraire planning = new PlanningHoraire();
        planning.setId(1L);
        
        ArrayList<PlanningHoraire> plannings = new ArrayList<>();
        plannings.add(planning);
        
        // Act
        user.setPlanningHoraires(plannings);

        // Assert
        assertNotNull(user.getPlanningHoraires());
        assertEquals(1, user.getPlanningHoraires().size());
    }

    @Test
    void testSousProjetsRelationship() {
        // Arrange
        SousProjet sousProjet = new SousProjet();
        sousProjet.setId(1L);
        
        ArrayList<SousProjet> sousProjets = new ArrayList<>();
        sousProjets.add(sousProjet);
        
        // Act
        user.setSousProjets(sousProjets);

        // Assert
        assertNotNull(user.getSousProjets());
        assertEquals(1, user.getSousProjets().size());
    }
}
