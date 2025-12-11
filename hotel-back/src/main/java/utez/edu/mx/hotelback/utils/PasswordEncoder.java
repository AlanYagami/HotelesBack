package utez.edu.mx.hotelback.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    public static String encodePassword(String rawPassword){
        return new BCryptPasswordEncoder().encode(rawPassword);

    }

    public static  boolean verifyPassword(String rawPassword,String encodedPassword){
        return new BCryptPasswordEncoder().matches(rawPassword, encodedPassword);

    }

    public static void main(String[] args) {
        String password = "password123";
        String encodedPassword = encodePassword(password);
        System.out.println("Encoded password: " + encodedPassword);

        boolean isMatch = verifyPassword(password, encodedPassword);
        System.out.println("Password match: " + isMatch);
    }
}
