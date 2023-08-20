package com.staroot.im.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staroot.im.entity.Employee;
import com.staroot.im.entity.User;
import com.staroot.im.repository.EmployeeRepository;
import com.staroot.im.repository.UserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Controller
public class EmployeeController {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Autowired
    private StringEncryptor stringEncryptor;
    @GetMapping("/api/emp/list")
    @ResponseBody
    public String encryptedUsers() {
        List<Employee> employees = new ArrayList<>();
        employees = employeeRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsondata = "";
        String encryptedJson = "";
        try {
            jsondata = objectMapper.writeValueAsString(employees);
            logger.debug("jsondata :: "+jsondata);
            encryptedJson = stringEncryptor.encrypt(jsondata);
            logger.debug("encryptedJson :: "+encryptedJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return encryptedJson;
    }

    @GetMapping("/api/emp/encchk")
    public String encryptedUsers2(Model model) {
        List<Employee> employees = new ArrayList<>();
        employees = employeeRepository.findAll();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsondata = "";
        String encryptedJson = "";
        String encryptedJsonComp = "";

        byte[] jsonBytes;
        byte[] compressedBytes;
        try {
            jsondata = objectMapper.writeValueAsString(employees);

            try {
                jsonBytes = jsondata.getBytes("UTF-8");
                compressedBytes = compress(jsonBytes);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            //logger.debug("jsondata :: "+jsondata);

            encryptedJson = stringEncryptor.encrypt(jsondata);
            encryptedJsonComp = stringEncryptor.encrypt(Arrays.toString(compressedBytes));

            //logger.debug("encryptedJson :: "+encryptedJson);
            model.addAttribute("compdata",compressedBytes);
            model.addAttribute("enccompdata",encryptedJsonComp);
            model.addAttribute("jsondata",jsondata);
            model.addAttribute("encdata",encryptedJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return "empdata";
    }
    @GetMapping("/api/emp/dectest")
    public String encryptedUsers3(Model model) {
        List<Employee> employees = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        employees = employeeRepository.findAll();

        String jsondata = "";
        String encryptedJson = "";
        try {
            jsondata = objectMapper.writeValueAsString(employees);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        encryptedJson = stringEncryptor.encrypt(jsondata);

        //model.addAttribute("jsondata",jsondata);
        model.addAttribute("encdata",encryptedJson);

        return "dectest";
    }
    @PostMapping("/api/emp/decrypt")
    @ResponseBody
    public String decryptData(@RequestBody String encryptedData) {
        String decryptedData = stringEncryptor.decrypt(encryptedData);
        return decryptedData;
    }
    @PostMapping("/api/emp/decrypt2")
    public String decryptData2(@RequestParam String encdata, Model model) {
        String decryptedData = stringEncryptor.decrypt(encdata);
        model.addAttribute("jsondata",decryptedData);
        model.addAttribute("encdata",encdata);
        return "dectest";
    }
    @PostMapping("/api/emp/decryptzip")
    @ResponseBody
    public String decryptDataZip(@RequestBody String encryptedData) {
        String decryptedData = stringEncryptor.decrypt(encryptedData);
        String decompressData = "";
        logger.debug("decryptedData::"+decryptedData);
        try {
            //decompressData = decompress(decryptedData.getBytes("ISO-8859-1"));
            byte[] compressedData = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, -1, -67, -50, -37, 10, -126, 64, 16, 6, -32, 119, -39, 107, 73, -41, -29, 26, 4, 65, 16, 20, 9, 97, -108, 88, 116, -31, 41, 21, 20, -45, -11, 16, 68, -17, -98, 22, 65, 12, 123, 61, 119, -1, -52, -16, 51, -33, -27, 73, -14, -104, -52, -87, 68, 58, -98, 52, 83, -4, 4, -123, 72, -28, 30, 112, 62, 84, -51, -76, -38, -46, 126, 127, -86, -69, 126, 8, 119, -2, 45, 75, 28, 119, -91, -55, -11, -95, -119, 35, -17, -24, -11, -113, 48, 91, -25, 86, -59, -27, -78, -111, -43, 104, 49, 54, 121, 80, -76, 99, -53, 53, -93, -42, 86, -100, 116, -29, -97, 51, 86, 48, 58, 94, -110, 50, -56, -117, -33, -113, 101, 58, 77, -77, -88, 42, -55, 75, -6, 58, 84, -32, -96, 8, 14, 42, 112, 104, -64, -95, 34, 56, 84, -127, 67, 7, 14, 13, -63, -95, 9, 28, 6, 112, -24, 8, 14, 93, -32, 48, -127, -61, 64, 112, 24, 2, -121, 5, 28, 38, -126, -61, 20, 56, 24, 112, 88, 8, 14, 75, -32, -80, -127, -125, 33, 56, -104, -64, 65, 21, 0, -79, 17, 32, -10, 63, -28, -6, 6, 120, -28, -15, 103, 82, 5, 0, 0};


            decompressData = decompress(compressedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return decompressData;
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(data.length);
        try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
            zipStream.write(data);
        }
        return byteStream.toByteArray();
    }

    private String decompress(byte[] compressedData) throws IOException {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedData);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteStream);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            String decompressedString = new String(output.toByteArray(), "UTF-8");

            return decompressedString;
        }
    }
}
