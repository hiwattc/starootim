package com.staroot.im.controller;
import com.staroot.im.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;

import com.staroot.im.service.VerificationService;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationService verificationService;
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @GetMapping("/test/sendEmail")
    public String sendVerificationEmail() {
        String toEmail = "starootmaster@gmail.com";
        String name = "starootmaster";

        String verificationCode = verificationService.generateVerificationCode(toEmail);

        //boolean result = verificationService.verifyCode(toEmail, verificationCode);
        //logger.debug("Email verification result: {}", result);

        logger.debug("verification code: {}", verificationCode);
        logger.debug("toEmail: {}", toEmail);
        logger.debug("name: {}", name);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verificationCode", verificationCode);
        emailService.sendHtmlEmail(toEmail, "Account Verification", "verification-email", context);
        return "Verification email sent successfully";
    }




    @GetMapping("/test/sendVerificationEmail")
    public String sendVerificationEmail(HttpServletRequest request, Model model,@RequestParam String toEmail) {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        logger.debug("session userid : "+userid);
        String name = userid;
        String verificationCode = verificationService.generateVerificationCode(toEmail);

        logger.debug("verification code: {}", verificationCode);
        logger.debug("toEmail: {}", toEmail);
        logger.debug("name: {}", name);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verificationCode", verificationCode);
        emailService.sendHtmlEmail(toEmail, "Account Verification", "verification-email", context);
        return "Verification email sent successfully";
    }

}
