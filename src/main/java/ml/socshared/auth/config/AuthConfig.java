package ml.socshared.auth.config;

import ml.socshared.auth.config.online.OnlineUsersStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OnlineUsersStore activeUserStore() {
        return new OnlineUsersStore();
    }

}
