package com.teamo.teamo.config;

import com.teamo.teamo.filter.JwtFilter;
import com.teamo.teamo.security.CustomOAuth2UserDetailService;
import com.teamo.teamo.service.JwtService;
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

    private final CustomOAuth2UserDetailService customOAuth2UserDetailService;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpbasic -> httpbasic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .authorizeHttpRequests(autho -> {
                    autho
                            .requestMatchers("/auth/reissue", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                            .requestMatchers("/**/admin").hasRole(AuthType.ROLE_ADMIN.getKey())
                            .requestMatchers("/**/guest").hasAnyRole(AuthType.ROLE_USER.getKey(), AuthType.ROLE_ADMIN.getKey())
                            .anyRequest().permitAll(); // 개발 단계라서
                })
                .oauth2Login(oauth -> {
                    oauth.userInfoEndpoint(end -> end.userService(customOAuth2UserDetailService))
                            .defaultSuccessUrl("/auth/login")
                            .failureUrl("/fail");
                })
                .addFilterBefore(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
