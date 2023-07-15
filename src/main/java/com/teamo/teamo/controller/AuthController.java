package com.teamo.teamo.controller;

import com.teamo.teamo.model.request.ReissueRequest;
import com.teamo.teamo.security.token.JwtDto;
import com.teamo.teamo.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public ResponseEntity<JwtDto> login(@AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("login 시작 = {}", (String)oAuth2User.getAttribute("email"));
        return ResponseEntity.ok(authService.login(oAuth2User));
    }

    @PostMapping("/reissue")
    public ResponseEntity<JwtDto> reissue(@RequestBody ReissueRequest request) {
        log.info("accessToken 재발행 시작");
        return ResponseEntity.ok(authService.reissue(request));
    }
}
