package com.staroot.im.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data // This annotation includes @Getter, @Setter, @EqualsAndHashCode, and @ToString
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String used;

    @Column(nullable = false)
    private int failCnt;

    @Column
    private LocalDateTime verifiedAt;
    @Column
    private LocalDateTime modifiedAt;

    public VerificationCode() {
        this.used = "N"; // Default value
        this.failCnt = 0; // Default value
    }

    public VerificationCode(String email, String code, LocalDateTime createdAt) {
        this.email = email;
        this.code = code;
        this.createdAt = createdAt;
        this.used = "N"; // Default value
        this.failCnt = 0; // Default value
    }

}
