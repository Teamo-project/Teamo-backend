package com.teamo.teamo.security;

import com.teamo.teamo.domain.Member;
import com.teamo.teamo.repository.MemberRepository;
import com.teamo.teamo.security.token.JwtDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final String AUTHORITIES_KEY = "auth";
    private final String BEARER_TYPE = "BEARER";
    private final Integer ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private final Integer REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    @Value("ZVc3Z0g4bm5TVzRQUDJxUXBIOGRBUGtjRVg2WDl0dzVYVkMyWW")
    private String secretKey;
    private Key key;

    private final MemberRepository memberRepository;

    @PostConstruct
    private void initialize() {
        // key 인코딩
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        key = Keys.hmacShaKeyFor(encodedKey.getBytes());
    }

    public JwtDto generateJwtDto(OAuth2User oAuth2User) {
        Date now = new Date();
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        Member member = memberRepository.findByEmail((String) oAuth2User.getAttribute("email"))
                .orElseThrow(() -> new RuntimeException("해당 email의 유저가 존재하지 않습니다"));

        String accessToken = generateAccessToken(member.getEmail(), member.getRole().getKey(), accessTokenExpiresIn);
        String refreshToken = generateRefreshToken(member.getEmail(), refreshTokenExpiresIn);

        return JwtDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public String generateAccessToken(String email, String role, Date accessExpireDate) {

        return Jwts.builder()
                .setSubject(email)
                .claim(AUTHORITIES_KEY, role)
                .setExpiration(accessExpireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String email, Date refreshExpireDate) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(refreshExpireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

}
