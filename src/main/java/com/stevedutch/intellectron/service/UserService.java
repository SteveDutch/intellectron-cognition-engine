
package com.stevedutch.intellectron.service;

import com.stevedutch.intellectron.domain.User;
import com.stevedutch.intellectron.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(String username, String email, String password) throws Exception {
        Optional<User> existingUserByUsername = userRepository.findByUsername(username);
        if (existingUserByUsername.isPresent()) {
            throw new Exception("Username already exists");
        }

        Optional<User> existingUserByEmail = userRepository.findByEmail(email);
        if (existingUserByEmail.isPresent()) {
            throw new Exception("Email already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        // Standardmäßig eine USER-Rolle zuweisen
        newUser.getRoles().add("USER");

        return userRepository.save(newUser);
    }
}

