package com.staroot.im.controller;

import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import com.staroot.im.util.PwdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/modify")
    public String modify(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        logger.debug("session userid : "+userid);

        User user = userRepository.findByUserid(userid);
        model.addAttribute("user", user);
        return "/profile/modify";
    }
    @PostMapping("/modify")
    public String processModify(@RequestParam String email, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        logger.debug("session userid : "+userid);
        User user = userRepository.findByUserid(userid);

        user.setEmail(email);
        userRepository.save(user);
        logger.debug("profile modify success!");
        model.addAttribute("user", user);
        model.addAttribute("message","Successfully Modified!");
        return "/profile/modify";
    }
    @GetMapping("/modifyPwd")
    public String modifyPwd(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        logger.debug("session userid : "+userid);

        String password1 = "";
        String password2 = "";
        model.addAttribute("password1", password1);
        model.addAttribute("password2", password2);
        return "/profile/modifyPwd";
    }
    @PostMapping("/modifyPwd")
    public String processModifyPwd(@RequestParam String password1,@RequestParam String password2, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        logger.debug("session userid : "+userid);
        User user = userRepository.findByUserid(userid);

        model.addAttribute("password1", password1);
        model.addAttribute("password2", password2);

        if(!password1.equals(password2)){
            model.addAttribute("message","both input pwd must be same!");
            return "/profile/modifyPwd";
        }
        if (PwdUtil.isValidPassword(password1)){
            String hashedPassword = PwdUtil.hashPassword(password1, user.getSalt());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            //logger.debug("Your password have been changed!");
            model.addAttribute("message","Your password have been changed!");
            model.addAttribute("passwordChanged", true);
            return "/profile/modifyPwd";
        }else{
            //logger.debug("Change Faild!, you have to check password condition!");
            model.addAttribute("message","Change Faild!, you have to check password condition!");
            return "/profile/modifyPwd";
        }
    }
}
