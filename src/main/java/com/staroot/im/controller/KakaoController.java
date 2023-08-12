package com.staroot.im.controller;

import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import com.staroot.im.service.KakaoLoginService;
import com.staroot.im.util.PwdUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/social")
public class KakaoController {
    private static final Logger logger = LoggerFactory.getLogger(KakaoController.class);

    private final KakaoLoginService kakaoLoginService;
    private final UserRepository userRepository;

    @Autowired
    public KakaoController(KakaoLoginService kakaoLoginService, UserRepository userRepository){
        this.kakaoLoginService = kakaoLoginService;
        this.userRepository = userRepository;
    }

    @GetMapping("/kakao")
    public String kakaoCallback(String code ,HttpServletRequest request, Model model) {
        String accessToken = kakaoLoginService.getAccessToken(code);
        //String memberInfo = kakaoLoginService.getMemberInfo(accessToken);
        User memberInfo = kakaoLoginService.getMemberInfo(accessToken);
        logger.debug("kakao code :"+code);
        logger.debug("kakao accessToken :"+accessToken);

        logger.debug("kakao memberInfo.getUserid() :"+memberInfo.getUserid());
        logger.debug("kakao memberInfo.getEmail() :"+memberInfo.getEmail());

        String userid = memberInfo.getUserid();


        //로그인처리
        User user = userRepository.findByUserid(userid);
        logger.debug("user.userid : "+user.getUserid());
        logger.debug("user.email : "+user.getEmail());

        if (user != null) {
            logger.debug("login success!");
            HttpSession session = request.getSession();
            session.setAttribute("userid", userid);
            model.addAttribute("userid",user.getUserid());
            return "redirect:/main";
        } else {
            logger.debug("kakao user registering...");
            userRepository.save(memberInfo);
            HttpSession session = request.getSession();
            session.setAttribute("userid", userid);
            model.addAttribute("userid",user.getUserid());
            return "redirect:/main";
        }
        //return "카카오에서 받은 Code정보 : "+ accessToken +"\r\n"+memberInfo;
    }

}
