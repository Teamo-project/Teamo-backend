package com.teamo.teamo.service;

import com.teamo.teamo.domain.Member;
import com.teamo.teamo.repository.MemberRepository;
import com.teamo.teamo.response.JwtDto;
import com.teamo.teamo.security.JwtProvider;
import com.teamo.teamo.type.AuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public JwtDto login(OAuth2User oAuth2User) {
        if (!memberRepository.existsByEmail((String) oAuth2User.getAttribute("email"))) {
            memberRepository.save(Member.builder()
                    .email((String) oAuth2User.getAttribute("email"))
                    .role(AuthType.ROLE_USER)
                    .build());
        }
        return jwtProvider.generateJwtDto(oAuth2User);
    }

}
