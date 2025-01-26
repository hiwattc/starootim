package com.staroot.im.repository;

import com.staroot.im.entity.VerificationCode;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByEmailAndCodeAndUsed(String email, String code, String used);
    VerificationCode findByEmailAndUsed(String email, String used);

    @Modifying
    @Transactional
    @Query("UPDATE VerificationCode v SET v.used = 'D', v.modifiedAt = :now WHERE v.email = :email and v.used='N'")
    void markAllAsUsedByEmail(String email, LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE VerificationCode v SET v.used = 'Z', v.modifiedAt = :now WHERE v.email = :email and v.used='N'")
    void markAllAsTrashedByEmail(String email, LocalDateTime now);
}
