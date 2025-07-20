package com.example.user.repository;

import com.example.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 유저 조회
    Optional<User> findByEmail(String email);

    // 이메일 존재 여부 체크
    boolean existsByEmail(String email);
}
