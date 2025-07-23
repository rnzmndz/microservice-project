package com.renzomendoza.employee_service.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {
    public static String getLocalIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }
}
