package com.example.lostfoundMS.services;

import com.example.lostfoundMS.entities.enums.Role;
import com.example.lostfoundMS.entities.User;
import com.example.lostfoundMS.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerUser(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("EMail already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
    }

    public User loginUser(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("No account found"));
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Incorrect password");
        }
        return user;
    }

}
