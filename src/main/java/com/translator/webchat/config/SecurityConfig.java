package com.translator.webchat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final UserAuthProvider userAuthProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .exceptionHandling(exception -> exception.authenticationEntryPoint(userAuthenticationEntryPoint))
                .addFilterBefore(new JwtAuthFilter(userAuthProvider), BasicAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers(HttpMethod.POST,
                                        "/v1/api/auth/signin",
                                        "/v1/api/auth/signup",
                                        "/v1/api/auth/refreshtoken",
                                        "/v1/api/auth/sendotp",
                                        "/v1/api/auth/verifyotpandchangepassword")
                                .permitAll()
                                .requestMatchers(
                                        antMatcher("/v3/api-docs/**"), // swagger
                                        antMatcher("/webjars/**"),         // swagger-ui webjars
                                        antMatcher("/swagger-resources/**"),  // swagger-ui resources
                                        antMatcher("/configuration/**"),      // swagger configuration
                                        antMatcher("/*.html"),
                                        antMatcher("/favicon.ico"),
                                        antMatcher("/**/*.html"),
                                        antMatcher("/**/*.css"),
                                        antMatcher("/**/*.js"),
                                        antMatcher("/ws/**")).permitAll()
                                .anyRequest().authenticated()
                ).build();
    }
}
