package com.example.lostfoundMS.repo;

import com.example.lostfoundMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUniversityId(String universityId);
    boolean existsByEmail(String email);
    boolean existsByUniversityId(String universityId);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByResetToken(String resetToken);
}
