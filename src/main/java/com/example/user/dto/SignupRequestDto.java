package com.example.user.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    public String email;
    public String password;
    public String nickname;
}

// 소셜 아이디 로그인 (ex. 카카오 아이디로 로그인하기 같은 것 구현할것인지) 구현 여부 정하기
// 아이디 형태(그냥 아이디, 이메일로 인증받아서 할 것인지)