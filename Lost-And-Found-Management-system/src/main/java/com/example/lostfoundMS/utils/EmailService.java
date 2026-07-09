package com.example.lostfoundMS.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        String link = baseUrl + "/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your Lost & Found account");
        message.setText(
                "Welcome! Click the link below to verify your account:\n\n" + link +
                        "\n\nThis link expires in 24 hours."
        );
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String link = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your Lost & Found password");
        message.setText(
                "Someone requested a password reset for this account.\n\n" +
                        "Click here to set a new password:\n" + link +
                        "\n\nThis link expires in 1 hour. If you didn't request this, ignore this email."
        );
        mailSender.send(message);
    }
}
