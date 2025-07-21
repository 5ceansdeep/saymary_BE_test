package com.example.user.controller;

import com.example.user.dto.LoginRequestDto;
import com.example.user.dto.SignupRequestDto;
import com.example.user.entity.User;
import com.example.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto, HttpServletRequest request) {
        try {
            User user = userService.login(dto.getEmail(), dto.getPassword());
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("nickname", user.getNickname()); // 아래 상태 확인창에 nickname을 띄워주기 위해서
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(e.getMessage()); // 인증 실패 시 401 반환
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    // 로그인 상태 확인
    @GetMapping("/me")
    public ResponseEntity<String> getLoginUser(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                return ResponseEntity.status(401).body("로그인되지 않음");
            }
            String nickname = (String) session.getAttribute("nickname");
            return ResponseEntity.ok("로그인 중: nickname=" + nickname);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류가 발생했습니다.");
        }
    }
}
