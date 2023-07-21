package com.teamo.teamo.filter;

import com.teamo.teamo.model.domain.Member;
import com.teamo.teamo.security.AuthConst;
import com.teamo.teamo.security.dto.MemberLoginDto;
import com.teamo.teamo.service.JwtService;
import com.teamo.teamo.type.AuthType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = jwtService.resolveToken(request);

        // debug 모드
        if (StringUtils.hasText(jwt) && jwt.equals(AuthConst.DEBUG_MODE)) {
            log.info("debug mode");
            // todo: debug 모드일 때 Context에 저장된 User의 Id 정보 어떻게 처리할지 고민하기
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    new MemberLoginDto(Member.builder()
                            .role(AuthType.ROLE_ADMIN)
                            .email("admin@gmail.com")
                            .build()),
                    "",
                    List.of(new SimpleGrantedAuthority(AuthType.ROLE_ADMIN.toString()))));
        }

        // access token 인가
        else if (StringUtils.hasText(jwt) && jwtService.validateAccessToken(jwt)) {
            Authentication authentication = jwtService.findAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
