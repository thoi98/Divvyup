package in.divvyup.config;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings ({ "unchecked" })
public class AuthConfig {
    @Autowired
    Config applicationConfig;

    @Bean
    public boolean getUseSecureCookie() {
        Map<String, Object> authConfig = (Map<String, Object>) applicationConfig.get("auth");
        return (Boolean) authConfig.getOrDefault("useSecureCookie", false);
    }

    @Bean
    public String getCookieDomain() {
        Map<String, Object> authConfig = (Map<String, Object>) applicationConfig.get("auth");
        return (String) authConfig.getOrDefault("cookieDomain", null);
    }

    @Bean
    public String getCookieName() {
        Map<String, Object> authConfig = (Map<String, Object>) applicationConfig.get("auth");
        return (String) authConfig.getOrDefault("cookieName", "quicklend-sid");
    }
}
