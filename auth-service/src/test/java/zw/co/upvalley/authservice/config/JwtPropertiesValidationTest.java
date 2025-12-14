package zw.co.upvalley.authservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import zw.co.upvalley.authservice.config.properties.JwtProperties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "app.security.jwt.secret=my-very-strong-secret-key-with-more-than-32-chars",
        "app.security.jwt.access-token-expiration-ms=900000",
        "app.security.jwt.refresh-token-expiration-ms=604800000"
})
class JwtPropertiesValidationTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void shouldLoadJwtPropertiesSuccessfully() {
        assertNotNull(jwtProperties.getSecret());
        assertTrue(jwtProperties.getSecret().length() >= 32);
        assertEquals(900000, jwtProperties.getAccessTokenExpirationMs());
        assertEquals(604800000, jwtProperties.getRefreshTokenExpirationMs());
        assertEquals("istms-auth-service", jwtProperties.getIssuer());
        assertEquals("istms-platform", jwtProperties.getAudience());
    }
}
