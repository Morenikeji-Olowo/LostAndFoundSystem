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

}
