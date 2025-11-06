package tn.esprit.PI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class PIApplicationTests {

    @Test
    void contextLoads() {
        // Test that the Spring context loads successfully
    }
}
