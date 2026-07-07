package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.entities.dto.RegisterRequest;
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
}
