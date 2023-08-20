package com.staroot.im.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/userList")
    public String userList(Model model) {
        List<User> users = getUsers(); // Replace this with your logic to fetch users from the database
        model.addAttribute("users", users);
        return "userList";
    }

    private List<User> getUsers() {
        // Replace this with your logic to fetch users from the database or any other data source
        List<User> users = new ArrayList<>();
        users = userRepository.findAll();
        return users;
    }
}
