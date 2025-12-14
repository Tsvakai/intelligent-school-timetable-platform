package zw.co.upvalley.authservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import zw.co.upvalley.authservice.config.properties.JwtProperties;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class PropertiesConfig {
    // This enables validation of configuration properties
}
