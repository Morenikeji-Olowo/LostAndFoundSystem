package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.entities.dto.RegisterRequest;
import com.example.lostfoundMS.entities.dto.ResetPasswordRequest;
import com.example.lostfoundMS.entities.enums.Role;
import com.example.lostfoundMS.repo.UserRepository;
import com.example.lostfoundMS.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder  passwordEncoder;

    @Autowired
    private EmailService  emailService;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        if (userRepository.existsByUniversityId(request.getUniversityId())) {
            throw new IllegalArgumentException("An account with this university ID already exists");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setUniversityId(request.getUniversityId());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.STUDENT);
        user.setEmailVerified(false);

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setVerificationExpiresAt(
                new Timestamp(System.currentTimeMillis() + (24 * 60 * 60 * 1000)) // 24 hours from now
        );

        User saved = userRepository.save(user);
        emailService.sendVerificationEmail(saved.getEmail(),saved.getVerificationToken());
        return saved;
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification link"));

        if (user.isEmailVerified()) {
            return;
        }

        if (user.getVerificationExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("This verification link has expired. Please request a new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpiresAt(null);
        userRepository.save(user);
    }

    public void resendVerificationEmail(String email){
        User user = userRepository.findByEmail(email).
            orElseThrow(() -> new IllegalArgumentException("No account found for this email"));

        if(user.isEmailVerified()) {
            throw new IllegalArgumentException("This account is already verified. You can log in.");
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationExpiresAt(
                new Timestamp(System.currentTimeMillis() + (24 * 60 * 60 * 1000))
        );
        userRepository.save(user);
        emailService.sendVerificationEmail(
                user.getEmail(), user.getVerificationToken()
        );
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiresAt(
                new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000)) // 1 hour, shorter than email verification
        );
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset link"));

        if (user.getResetTokenExpiresAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalArgumentException("This reset link has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiresAt(null);
        userRepository.save(user);
    }
}
