package com.teamo.teamo.controller;

import com.teamo.teamo.security.token.JwtDto;
import com.teamo.teamo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<JwtDto> login(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return ResponseEntity.ok(authService.login(oAuth2User));
    }
}
