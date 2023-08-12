package com.staroot.im.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PwdUtil {
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] combinedBytes = new byte[saltBytes.length + passwordBytes.length];
            System.arraycopy(saltBytes, 0, combinedBytes, 0, saltBytes.length);
            System.arraycopy(passwordBytes, 0, combinedBytes, saltBytes.length, passwordBytes.length);
            byte[] encodedHash = digest.digest(combinedBytes);
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean checkPasswordHash(String password, String salt, String storedHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] combinedBytes = new byte[saltBytes.length + passwordBytes.length];
            System.arraycopy(saltBytes, 0, combinedBytes, 0, saltBytes.length);
            System.arraycopy(passwordBytes, 0, combinedBytes, saltBytes.length, passwordBytes.length);
            byte[] encodedHash = digest.digest(combinedBytes);
            return Base64.getEncoder().encodeToString(encodedHash).equals(storedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidPassword(String password) {
        // 암호는 8자리 이상이어야 함
        if (password.length() < 8) {
            return false;
        }

        // 암호는 알파벳, 숫자, 특수기호 조합이어야 함
        String pattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/~`]).*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(password);
        if (!matcher.matches()) {
            return false;
        }

        // 연속되는 3글자 이상은 안됨
        for (int i = 0; i < password.length() - 2; i++) {
            char firstChar = password.charAt(i);
            char secondChar = password.charAt(i + 1);
            char thirdChar = password.charAt(i + 2);

            if ((Character.isLetter(firstChar) && Character.isLetter(secondChar) && Character.isLetter(thirdChar))
                    || (Character.isDigit(firstChar) && Character.isDigit(secondChar) && Character.isDigit(thirdChar))) {
                if (Math.abs(firstChar - secondChar) == 1 && Math.abs(secondChar - thirdChar) == 1) {
                    return false;
                }
            }
        }

        return true;
    }
}
