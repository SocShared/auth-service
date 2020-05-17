package ml.socshared.service.auth.config.security;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.config.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Profile({Constants.DEV_PROFILE, Constants.PROD_PROFILE, Constants.LOCAL_PROFILE})
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Run LOCAL/TEST Security Configuration");
        http.
                csrf().disable()
                .authorizeRequests().anyRequest().permitAll();
    }
}
