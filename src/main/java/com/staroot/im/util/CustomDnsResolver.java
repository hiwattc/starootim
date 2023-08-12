package com.staroot.im.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class CustomDnsResolver {

    private static Map<String, String> customDNSMap = new HashMap<>();

    // 원하는 도메인에 대해 커스텀 IP 주소를 매핑합니다.
    static {
        customDNSMap.put("staroot.com", "127.0.0.1");
        // 필요한 다른 도메인과 IP 주소도 추가할 수 있습니다.
    }

    public static InetAddress resolveDNS(String host) throws UnknownHostException {
        if (customDNSMap.containsKey(host)) {
            String ipAddress = customDNSMap.get(host);
            return InetAddress.getByName(ipAddress);
        }
        return InetAddress.getByName(host);
    }
}

