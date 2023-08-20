package com.staroot.im.jwt;

import com.staroot.im.controller.SysItemController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/jwt")
public class JwtController {
    private static final Logger logger = LoggerFactory.getLogger(JwtController.class);

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/api/public")
    public String publicEndpoint() {
        return "Public endpoint";
    }

    @PostMapping("/api/token/refresh")
    public String login(@RequestBody String refreshToken) {
        if (jwtUtils.validateJwtToken(refreshToken)) {
            String userid = jwtUtils.getUsernameFromJwtToken(refreshToken);
            String token = jwtUtils.generateJwtToken(userid);
            String refreshtoken = jwtUtils.generateRefreshToken(userid);

            logger.debug("refresh userid: " + userid);
            logger.debug("refresh token: " + token);
            logger.debug("refresh refreshtoken: " + refreshtoken);
            return token+"/"+refreshtoken;
        } else {
            return "Invalid token";
        }
    }

    @GetMapping("/api/token")
    public String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        logger.debug("userid for jwt token::"+userid);
        String token = jwtUtils.generateJwtToken(userid);
        String refreshtoken = jwtUtils.generateRefreshToken(userid);
        logger.debug("jwt token::"+token);
        logger.debug("jwt refreshtoken::"+refreshtoken);
        String useridFromToken = jwtUtils.getUsernameFromJwtToken(token);
        logger.debug("useridFromToken::"+useridFromToken);
        return token+"/"+refreshtoken;
    }

    @GetMapping("/api/token/test")
    public String login(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String userid = (String) session.getAttribute("userid");
        logger.debug("userid for jwt token::"+userid);

        String token = jwtUtils.generateJwtToken(userid);
        String refreshtoken = jwtUtils.generateRefreshToken(userid);

        logger.debug("jwt token::"+token);
        logger.debug("jwt refreshtoken::"+refreshtoken);

        String useridFromToken = jwtUtils.getUsernameFromJwtToken(token);
        logger.debug("useridFromToken::"+useridFromToken);


        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","Application/json");
        headers.set("Authorization", "Bearer " + token);


        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = "http://localhost:8080/jwt/api/private";


        HttpEntity req = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET, // or HttpMethod.POST
                req,
                String.class
        );
        /*
        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET, // or HttpMethod.POST
                null,
                String.class,
                headers
        );*/

        String responseBody = "";
        if (response.getStatusCode().is2xxSuccessful()) {
            responseBody = response.getBody();
            logger.debug("**Response: " + responseBody);
        } else {
            logger.error("**Request failed with status code: " + response.getStatusCodeValue());
        }

        return "JWT Token : " + token + "/" + useridFromToken+"/responsebody:"+responseBody;
    }

    @GetMapping("/api/private")
    public String privateEndpoint(@RequestHeader("Authorization") String authHeader) {
        logger.debug("/api/private called~~   authHeader:: "+ authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUsernameFromJwtToken(token);
                return "Private endpoint accessed by: " + username;
            } else {
                return "Invalid token";
            }
        } else {
            return "Missing or invalid authorization header";
        }
    }

}



