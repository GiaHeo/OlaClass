package com.example.olaclass.utils;

import java.security.SecureRandom;

public class InviteCodeUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int DEFAULT_LENGTH = 6;
    private static final int MAX_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    // Sinh mã code ngẫu nhiên, độ dài 6-8 ký tự
    public static String generateCode() {
        int length = DEFAULT_LENGTH + random.nextInt(MAX_LENGTH - DEFAULT_LENGTH + 1);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
