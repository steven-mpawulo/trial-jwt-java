package com.example.trialjwt.controllers;

import com.example.trialjwt.Utils.JwtUtil;
import com.example.trialjwt.models.User;
import com.example.trialjwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @PostMapping("/auth/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody User user) {
        User.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        User savedUser = userRepository.save(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", savedUser.getId());
        String token = jwtUtil.generateToken(claims, savedUser.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("body", savedUser);
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        List<User> users = userRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("body", users);
        return users;

    }
}
