package com.example.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users") // erd에서 고객부분
public class User {

    @Id // id = pk(기본키), JPA가 자동으로 채워줌
    //GeneratedValue -> 기본키 어떻게 생성, GenerationType.IDENTITY -> DB의 auto_increment(자동 증가)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // 암호화된 비밀번홎 저장

    @Column(nullable = false)
    private String nickname;

    @Column(unique = true)
    private String resetToken; // 비밀번호 재설정 토큰

    private LocalDateTime resetTokenExpiresAt; // 토큰 만료시간

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // 생성자 (회원가입 시 필요한 값들만 받기)
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
