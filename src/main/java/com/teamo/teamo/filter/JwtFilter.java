package com.teamo.teamo.filter;

import com.teamo.teamo.security.AuthConst;
import com.teamo.teamo.security.JwtProvider;
import com.teamo.teamo.type.AuthType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = jwtProvider.resolveToken(request);

        // debug 모드
        if (StringUtils.hasText(jwt) && jwt.equals(AuthConst.DEBUG_MODE)) {
            log.info("debug mode");
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(AuthType.ROLE_ADMIN.toString()));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(new User("admin@gmail.com","",authorities),
                            "",
                            authorities));
        }

        // access token 인가
        else if (StringUtils.hasText(jwt) && jwtProvider.validateAccessToken(jwt)) {
            Authentication authentication = jwtProvider.findAuthentication(jwt);
            log.info("login User = {}", authentication.getPrincipal());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
