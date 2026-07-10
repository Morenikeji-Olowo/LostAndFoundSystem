package com.example.lostfoundMS.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String link = baseUrl + "/verify-email?token=" + token;
        String html = "<p>Welcome! Click the link below to verify your account:</p>"
                + "<p><a href=\"" + link + "\">" + link + "</a></p>"
                + "<p>This link expires in 24 hours.</p>";

        send(toEmail, "Verify your Lost & Found account", html);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String link = baseUrl + "/reset-password?token=" + token;
        String html = "<p>Someone requested a password reset for this account.</p>"
                + "<p><a href=\"" + link + "\">Click here to set a new password</a></p>"
                + "<p>This link expires in 1 hour. If you didn't request this, ignore this email.</p>";

        send(toEmail, "Reset your Lost & Found password", html);
    }
    private void send(String toEmail, String subject, String html) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fromEmail);
            payload.put("to", List.of(toEmail));
            payload.put("subject", subject);
            payload.put("html", html);

            String jsonBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException(
                        "Resend API error (" + response.statusCode() + "): " + response.body()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email via Resend", e);
        }
    }
}
