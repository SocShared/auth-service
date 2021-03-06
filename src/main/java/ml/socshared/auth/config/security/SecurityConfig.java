package ml.socshared.auth.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ml.socshared.auth.config.security.jwt.JwtAuthenticationEntryPoint;
import ml.socshared.auth.config.security.jwt.JwtConfigurer;
import ml.socshared.auth.config.Constants;
import ml.socshared.auth.service.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@Profile({Constants.DEV_PROFILE, Constants.PROD_PROFILE})
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String PRIVATE_ENDPOINT = "/api/v1/private/**";
    private static final String OAUTH_ENDPOINT = "/oauth/**";
    private static final String PUBLIC_ENDPOINT = "/api/v1/public/**";
    private static final String PROTECTED_ENDPOINT = "/api/v1/protected/**";

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Run DEV/PROD Security Configuration");
        http.
                httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(PUBLIC_ENDPOINT, OAUTH_ENDPOINT).permitAll()
                .antMatchers(PRIVATE_ENDPOINT, PROTECTED_ENDPOINT).authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                .and()
                .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());
    }
}
