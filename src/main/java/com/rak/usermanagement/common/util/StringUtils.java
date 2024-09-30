package com.rak.usermanagement.common.util;

public class StringUtils {

    public static boolean isValidPassword(String password) {
        return password != null && password.matches("^[a-zA-Z0-9]{8,}$");
    }
}
