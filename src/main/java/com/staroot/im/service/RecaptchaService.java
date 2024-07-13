package com.staroot.im.service;

import com.staroot.im.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key}")
    private String secretKey;
    @Value("${recaptcha_v3.secret.key}")
    private String secretKey3;

    private final String recaptchaEndpoint = "https://www.google.com/recaptcha/api/siteverify";
    private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);

    public boolean verify(String response) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", response);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<RecaptchaResponse> recaptchaResponseEntity = restTemplate.postForEntity(
                recaptchaEndpoint, requestEntity, RecaptchaResponse.class);

        RecaptchaResponse recaptchaResponse = recaptchaResponseEntity.getBody();

        logger.debug("recaptchaResponse.getErrorMessage(v2) ::"+recaptchaResponse.getErrorMessage());
        return recaptchaResponse != null && recaptchaResponse.isSuccess();
    }
    public boolean verifyForV3(String response) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey3);
        body.add("response", response);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<RecaptchaResponse> recaptchaResponseEntity = restTemplate.postForEntity(
                recaptchaEndpoint, requestEntity, RecaptchaResponse.class);

        RecaptchaResponse recaptchaResponse = recaptchaResponseEntity.getBody();

        logger.debug("recaptchaResponse.getErrorMessage(v3) ::"+recaptchaResponse.getErrorMessage());
        return recaptchaResponse != null && recaptchaResponse.isSuccess();
    }

}

