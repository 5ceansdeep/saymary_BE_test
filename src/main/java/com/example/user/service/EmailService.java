package com.example.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    public final JavaMailSender mailSender;

    public void sendResetEmail(String to, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("비밀번호 재설정 안내");
        message.setText("아래 링크를 클릭해 비밀번호를 재설정하세요:\n" + link);
        mailSender.send(message);
    }
}
