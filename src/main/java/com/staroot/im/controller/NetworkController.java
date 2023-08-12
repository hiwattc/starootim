package com.staroot.im.controller;

import com.staroot.im.entity.User;
import com.staroot.im.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/net")
public class NetworkController {


    @GetMapping("/test1")
    public String userList(Model model) {

        //아래 네트워크 설정은 적용이 안되는듯함
        //적용해도 달라지는게 없음

        // 커스텀 DNS Resolver 등록
        //Security.setProperty("networkaddress.cache.ttl", "0");
        //System.setProperty("sun.net.spi.nameservice.provider.1", "dns,CustomDnsResolver");

        // 커스텀 DNS 서버 주소 설정
        //String customDNSServer = "7.8.8.7";
        //System.setProperty("sun.net.spi.nameservice.nameservers", customDNSServer);

        try {
            String domain = "staroot.com";
            InetAddress resolvedAddress = InetAddress.getByName(domain);
            String ipAddress = resolvedAddress.getHostAddress();
            System.out.println("Resolved IP address for " + domain + ": " + ipAddress);
        } catch (UnknownHostException e) {
            System.err.println("Failed to resolve IP address for example.com");
            e.printStackTrace();
        }



        model.addAttribute("response", "hihi");
        return "userList";
    }


}
