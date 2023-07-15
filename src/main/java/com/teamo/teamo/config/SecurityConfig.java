package com.teamo.teamo.config;

import com.teamo.teamo.filter.JwtFilter;
import com.teamo.teamo.security.CustomUserDetailService;
import com.teamo.teamo.security.JwtProvider;
import com.teamo.teamo.type.AuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpbasic -> httpbasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .authorizeHttpRequests(autho -> {
                    autho
                            .requestMatchers("/auth/reissue").permitAll()
                            .requestMatchers("/**/admin").hasRole(AuthType.ROLE_ADMIN.getKey())
                            .requestMatchers("/**/guest").hasAnyRole(AuthType.ROLE_USER.getKey(), AuthType.ROLE_ADMIN.getKey())
                            .anyRequest().permitAll();
                })
                .oauth2Login(oauth -> {
                    oauth.userInfoEndpoint(end -> end.userService(customUserDetailService))
                            .defaultSuccessUrl("/auth/login")
                            .failureUrl("/fail");
                })
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
