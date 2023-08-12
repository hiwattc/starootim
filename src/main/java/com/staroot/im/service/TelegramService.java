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
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(TelegramService.class);

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.chat.id}")
    private String chatId;
    public void sendTelegramMessage(){
        String apiUrl = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage?chat_id=" + chatId;
        String emoji = "\uD83D\uDE0A \uD83C\uDF26  🔥☃️📛☂︎🩸"; // This is the smiley face emoji

        String text = "hello this is springboot!!\n"+emoji+"hello\n"+emoji+"1234\n1234 ";
        String requestBody = "{ \"text\": \"" + text + text + "\"}";
        sendPostRequest(apiUrl, requestBody);
    }

    public void sendPostRequest(String url, String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);

        System.out.println("Response: " + responseEntity.getBody());
    }
}
