package com.staroot.im.service;

import com.google.gson.Gson;
import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import com.staroot.im.util.PwdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoLoginService {
    @Value("${kakao.default.password}")
    private String kakaoPassword;
    @Value("${kakao.default.clientid}")
    private String clientId;
    @Value("${kakao.default.redirecturi}")
    private String redirecturi;

    @Value("${kakao.default.url.oauth}")
    private String kakaoOauthApiUrl;
    @Value("${kakao.default.url.api}")
    private String kakaoApiUrl;

    private final UserRepository userRepository;

    public KakaoLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getAccessToken(String code){
        HttpHeaders header = new HttpHeaders();
        header.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","authorization_code");
        body.add("client_id",clientId);
        body.add("redirect_uri",redirecturi);
        body.add("code",code);

        HttpEntity<MultiValueMap<String,String>> requestEntity=new HttpEntity<>(body,header);
        RestTemplate rstTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = rstTemplate.exchange(kakaoOauthApiUrl,
                HttpMethod.POST,requestEntity, String.class);

        String jsonData = responseEntity.getBody();
        Gson gsonObj = new Gson();
        Map<?,?> data = gsonObj.fromJson(jsonData, Map.class);

        //return responseEntity.getBody();
        return (String)data.get("access_token");
    }
    public User getMemberInfo(String accessToken){
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization","Bearer "+accessToken);
        header.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(header);
        RestTemplate rstTemplate = new RestTemplate();

        ResponseEntity<String> responseEntity = rstTemplate.exchange(kakaoApiUrl,
                HttpMethod.POST,requestEntity, String.class);

        String memberInfo = responseEntity.getBody();
        Gson gsonObj = new Gson();
        Map<?,?> data = gsonObj.fromJson(memberInfo, Map.class);

        String nickname = (String) ((Map<?,?>)(data.get("properties"))).get("nickanme");
        //String email = (String) ((Map<?,?>)(data.get("properties"))).get("email");
        String email = (String) ((Map<?,?>)(data.get("kakao_account"))).get("email");

        String salt = PwdUtil.generateSalt();
        String hashedPassword = PwdUtil.hashPassword(kakaoPassword, salt);

        User user = new User();
        user.setUserid(email);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        user.setEmail(email);
        
        //카카오 정보로 가입하기
        User dbuser = userRepository.findByUserid(email);
        if(dbuser == null){
            userRepository.save(user);
        }

        //return responseEntity.getBody();
        return user;
    }
}
