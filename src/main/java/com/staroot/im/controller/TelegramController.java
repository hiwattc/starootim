package com.staroot.im.controller;

import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import com.staroot.im.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/telegram")
public class TelegramController {

    @Autowired
    TelegramService telegramService;
    @GetMapping("/test1")
    @ResponseBody
    public String sendTelegram(Model model) {
        telegramService.sendTelegramMessage();
        return "ok";
    }

}
