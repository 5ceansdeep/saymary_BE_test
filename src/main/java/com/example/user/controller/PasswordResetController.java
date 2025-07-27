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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiresAt(LocalDateTime.now().plusMinutes(10)); // 10분만 토큰 유효 -> 10분안에 인증받고 변경해야함
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

        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰"));
        if (user.getResetTokenExpiresAt().isBefore(LocalDateTime.now()))
            throw new RuntimeException("토큰 만료됨");

        user.setPassword(newPassword); // (실제로는 BCrypt 암호화 필요)
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);

        return ResponseEntity.ok("비밀번호가 변경되었습니다!");
    }
}
