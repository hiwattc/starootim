package com.staroot.im.controller;

import com.staroot.im.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/verification")
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @RequestMapping("/generate")
    public String generateVerificationCode(@RequestParam String email) {
        return verificationService.generateVerificationCode(email);
    }

    @RequestMapping("/verify")
    public boolean verifyCode(@RequestParam String email, @RequestParam String code) {
        return verificationService.verifyCode(email, code);
    }
}
