package com.teamo.teamo.security;

import com.teamo.teamo.response.JwtDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtProvider {

    private final String AUTHORITIES_KEY = "AUTHORIZATION";
    private final String BEARER_TYPE = "BEARER";
    private final Integer ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;
    private final Integer REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;

    @Value("ZVc3Z0g4bm5TVzRQUDJxUXBIOGRBUGtjRVg2WDl0dzVYVkMyWW")
    private String secretKey;
    private Key key;

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

        String accessToken = generateAccessToken((String) oAuth2User.getAttribute("email"), accessTokenExpiresIn);
        String refreshToken = generateRefreshToken(refreshTokenExpiresIn);

        return JwtDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    public String generateAccessToken(String email, Date accessExpireDate) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(accessExpireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(Date refreshExpireDate) {
        return Jwts.builder()
                .setExpiration(refreshExpireDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}
