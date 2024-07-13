package com.staroot.im.controller;

import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import com.staroot.im.service.RecaptchaService;
import com.staroot.im.util.PwdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Value("${kakao.default.clientid}")
    private String clientId;
    @Value("${kakao.default.redirecturi}")
    private String redirecturi;

    @Autowired
    private RecaptchaService recaptchaService;

    private final UserRepository userRepository;
    @Autowired
    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("kakao_clientid",clientId);
        model.addAttribute("kakao_redirecturi",redirecturi);
        return "login";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return "redirect:/login";
    }


    @GetMapping("/main")
    public String showMainPage(HttpServletRequest request,Model model) {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        logger.debug("session userid : "+userid);
        User user = userRepository.findByUserid(userid);

        if(user == null){
            return "redirect:/login";
        }
        model.addAttribute("userid",user.getUserid());
        model.addAttribute("email",user.getEmail());
        return "main";
    }
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String userid,
            @RequestParam String password,
            @RequestParam(value = "g-recaptcha-response",defaultValue = "") String recaptchaResponse,
            @RequestParam(value = "g-recaptcha-response3",defaultValue = "") String recaptchaResponse3,
            HttpServletRequest request,
            Model model) {
        // In a real application, you would validate the user against the database or any other authentication mechanism.
        // For demonstration purposes, let's assume we have a user with username "user1" and the following salt and hashed password.
        model.addAttribute("kakao_clientid",clientId);
        model.addAttribute("kakao_redirecturi",redirecturi);

        /* reCAPTCHA v2 */
        if (!recaptchaService.verify(recaptchaResponse)) {
            logger.debug("recaptchaResponse(v2) : "+recaptchaResponse.toString());
            logger.debug("reCAPTCHA(v2) validation failed.!");
            model.addAttribute("errorMessage(v2)", "reCAPTCHA validation failed.");
            return "login";
        }else{
            logger.debug("reCAPTCHA(v2) validation success!");
        }
        /* reCAPTCHA v3 */
        if (!recaptchaService.verifyForV3(recaptchaResponse3)) {
            logger.debug("recaptchaResponse(v3) : "+recaptchaResponse3.toString());
            logger.debug("reCAPTCHA(v3) validation failed.!");
            model.addAttribute("errorMessage(v3)", "reCAPTCHA validation failed.");
            //return "login";
        }else{
            logger.debug("reCAPTCHA(v3) validation success!");
        }
        User user = userRepository.findByUserid(userid);
        if(user == null){
            logger.debug("login fail!");
            model.addAttribute("errorMessage", "존재하지 않는 ID입니다.");
            return "login";
        }

        logger.debug("user.userid : "+user.getUserid());
        logger.debug("user.email : "+user.getEmail());
        logger.debug("user.password : "+user.getPassword());
        String hashedPassword = PwdUtil.hashPassword(password, user.getSalt());

        if (hashedPassword.equals(user.getPassword())) {
            logger.debug("login success!");
            HttpSession session = request.getSession();
            session.setAttribute("userid", userid);
            return "redirect:/main";
        } else {
            logger.debug("login fail!");
            model.addAttribute("errorMessage", "사용자 ID 또는 암호를 확인해주세요.");
            return "login";
        }
    }


    @GetMapping("/register")
    public String showRegistrationPage() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@RequestParam String userid, @RequestParam String password,@RequestParam String email, Model model) {
        String salt = PwdUtil.generateSalt();
        String hashedPassword = PwdUtil.hashPassword(password, salt);

        User user = new User();
        user.setUserid(userid);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setEmail(email);
        userRepository.save(user);

        // Redirect to the login page after successful registration
        return "redirect:/login";
    }

}

