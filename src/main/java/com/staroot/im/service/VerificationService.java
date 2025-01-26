package com.staroot.im.service;

import com.staroot.im.entity.VerificationCode;
import com.staroot.im.repository.VerificationCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class VerificationService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 3;
    private static final int MAX_FAIL_COUNT = 3;

    public String generateVerificationCode(String email) {
        String code = generateRandomCode();
        VerificationCode verificationCode = new VerificationCode(email, code, LocalDateTime.now());

        //update used='Z' if previous unused verification code exists
        LocalDateTime now = LocalDateTime.now();
        verificationCodeRepository.markAllAsTrashedByEmail(email, now);

        //register new verification code after organizing previous code for prevent hacking
        verificationCodeRepository.save(verificationCode);
        return code;
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmailAndCodeAndUsed(email, code, "N");
        if (verificationCode == null) {

            //increment failed cnt
            verificationCode = verificationCodeRepository.findByEmailAndUsed(email, "N");
            if (verificationCode == null) {
                return false;
            }else{
                incrementFailCount(verificationCode);
            }
            return false;
        }
        boolean isValid = verificationCode.getCreatedAt().plusMinutes(EXPIRATION_MINUTES).isAfter(LocalDateTime.now());
        if (isValid) {
            // update again
            verificationCode.setUsed("Y");
            verificationCode.setVerifiedAt(LocalDateTime.now());
            verificationCode.setModifiedAt(LocalDateTime.now());
            verificationCodeRepository.save(verificationCode);

            // Mark all verification codes for this email as used
            LocalDateTime now = LocalDateTime.now();
            verificationCodeRepository.markAllAsUsedByEmail(email, now);

        }
        return isValid;
    }
    private void incrementFailCount(VerificationCode verificationCode) {
        LocalDateTime now = LocalDateTime.now();
        verificationCode.setModifiedAt(LocalDateTime.now());
        verificationCode.setFailCnt(verificationCode.getFailCnt() + 1);
        if (verificationCode.getFailCnt() >= MAX_FAIL_COUNT) {
            verificationCode.setUsed("F");
        }
        verificationCodeRepository.save(verificationCode);
    }
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
