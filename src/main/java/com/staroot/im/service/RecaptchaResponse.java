package com.staroot.im.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RecaptchaResponse {

    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    private String hostname;

    // v3 전용
    private Float score;
    private String action;

    @JsonProperty("error-codes")
    private List<String> errorCodes;

    // 기본 생성자
    public RecaptchaResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    public void setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
    }

    // 오류 메시지 가져오기
    public String getErrorMessage() {
        if (errorCodes == null || errorCodes.isEmpty()) {
            return null;
        }
        return String.join(", ", errorCodes);
    }

    @Override
    public String toString() {
        return "RecaptchaResponse{" +
                "success=" + success +
                ", challengeTs='" + challengeTs + '\'' +
                ", hostname='" + hostname + '\'' +
                ", score=" + score +
                ", action='" + action + '\'' +
                ", errorCodes=" + errorCodes +
                '}';
    }
}
