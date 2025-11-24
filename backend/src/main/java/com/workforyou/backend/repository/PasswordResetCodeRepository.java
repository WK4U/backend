package com.workforyou.backend.repository;

import com.workforyou.backend.model.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findByUsuarioEmailAndCode(String email, String code);
    Optional<PasswordResetCode> findByCode(String code);
}

