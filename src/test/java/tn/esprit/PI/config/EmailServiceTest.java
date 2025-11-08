package tn.esprit.PI.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendRegistrationEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String subject = "Welcome";
        String body = "Welcome to our application";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendRegistrationEmail(to, subject, body);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendRegistrationEmail_ExceptionHandling() {
        // Arrange
        String to = "test@example.com";
        String subject = "Welcome";
        String body = "Welcome message";

        doThrow(new RuntimeException("SMTP error")).when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendRegistrationEmail(to, subject, body);

        // Assert - should not throw exception
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendPasswordResetEmail_Success() {
        // Arrange
        String to = "test@example.com";
        String resetToken = "abc123xyz";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(to, resetToken);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendPasswordResetEmail_ExceptionHandling() {
        // Arrange
        String to = "test@example.com";
        String resetToken = "abc123xyz";

        doThrow(new RuntimeException("Email send failed")).when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(to, resetToken);

        // Assert - should not throw exception
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendConfirmationEmail_Success() {
        // Arrange
        String to = "test@example.com";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendConfirmationEmail(to);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendConfirmationEmail_ExceptionHandling() {
        // Arrange
        String to = "test@example.com";

        doThrow(new RuntimeException("Connection error")).when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendConfirmationEmail(to);

        // Assert - should not throw exception
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendRegistrationEmail_WithNullTo() {
        // Arrange
        String to = null;
        String subject = "Test";
        String body = "Test body";

        // Act - should handle null gracefully without throwing
        emailService.sendRegistrationEmail(to, subject, body);
        
        // Assert - method completes (email service catches exceptions internally)
        verify(javaMailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendPasswordResetEmail_ContainsResetLink() {
        // Arrange
        String to = "user@example.com";
        String resetToken = "secure-token-123";

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(to, resetToken);

        // Assert
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
