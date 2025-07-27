package com.example.user.controller;

import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final EmailService emailService;

    // 1. 비밀번호 재설정 요청
    @PostMapping("/request-reset")
public ResponseEntity<?> requestReset(@RequestBody Map<String, String> req) {
    String email = req.get("email");

    // 사용자 존재 여부 검사
    User user = userRepository.findByEmail(email).orElse(null);
    if (user == null) {
        return ResponseEntity.badRequest().body("존재하지 않는 이메일입니다.");
    }

    String token = UUID.randomUUID().toString();
    user.setResetToken(token);
    user.setResetTokenExpiresAt(LocalDateTime.now().plusMinutes(10));
    userRepository.save(user);

    String resetUrl = "http://localhost:8080/reset-password?token=" + token;
    emailService.sendResetEmail(email, resetUrl);

    return ResponseEntity.ok("비밀번호 재설정 메일 발송됨");
}


    // 2. 토큰 검증 및 비밀번호 재설정
    @PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
    String token = req.get("token");
    String newPassword = req.get("newPassword");

    // [1] 필수 항목 누락 확인
    if (token == null || newPassword == null) {
        return ResponseEntity.badRequest().body("필수 항목 누락");
    }

    // [2] 사용자 조회
    User user = userRepository.findByResetToken(token)
            .orElse(null);
    if (user == null) {
        return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
    }

    // [3] 토큰 만료 확인
    if (user.getResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
        return ResponseEntity.badRequest().body("토큰이 만료되었습니다.");
    }

    // [4] 비밀번호 설정
    user.setPassword(newPassword); // (추후 BCrypt 적용)
    user.setResetToken(null);
    user.setResetTokenExpiresAt(null);
    userRepository.save(user);

    return ResponseEntity.ok("비밀번호가 변경되었습니다!");
}
}
