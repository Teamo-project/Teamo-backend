package com.teamo.teamo.domain;

import com.teamo.teamo.type.AuthType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Table(name = "MEMBER_TABLE")
@NoArgsConstructor
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private AuthType role;

    @Builder
    public Member(String email, AuthType role) {
        this.email = email;
        this.role = role;
    }
}
