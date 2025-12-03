package com.onat.jurist.lawyer.repository;

import com.onat.jurist.lawyer.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByHashedToken(String hashedToken);
}
