package com.LMS.Learning_Management_System.config;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class PasswordEncryption {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Encrypt the password "12345"
        String encryptedPassword = passwordEncoder.encode("12345");
        // Print the encrypted password
        System.out.println("Encrypted Password: " + encryptedPassword);
    }
}