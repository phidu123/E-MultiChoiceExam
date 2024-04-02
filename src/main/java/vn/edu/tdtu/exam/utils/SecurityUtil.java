package vn.edu.tdtu.exam.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecurityUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SALT = "java123";

    public static String getHashPassword(String password) {
        String saltedPassword = password + SALT;
        return passwordEncoder.encode(saltedPassword);
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword+SALT, encodedPassword);
    }
}
