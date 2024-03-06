package com.example.trialjwt.controllers;

import com.example.trialjwt.Utils.JwtUtil;
import com.example.trialjwt.models.User;
import com.example.trialjwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @PostMapping("/auth/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody Map<String, Object> jsonData) {
        String password = (String) jsonData.get("password");
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(
                (String) jsonData.get("firstName"),
                (String) jsonData.get("lastName"),
                hashedPassword,
                (String) jsonData.get("email")

                );
        User savedUser = userRepository.save(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", savedUser.getId());
        String token = jwtUtil.generateToken(claims, savedUser.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("auth/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody Map<String, Object> jsonData) {
        String email = (String) jsonData.get("email");
        String password = (String) jsonData.get("password");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = (User) authentication.getPrincipal();
        Optional<User> userFromDatabase = userRepository.findUserByEmail(user.getUsername());
        if (userFromDatabase.isPresent()) {
            User actualUser = userFromDatabase.get();
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", actualUser.getId());
            String token = jwtUtil.generateToken(claims, actualUser.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers() {
        List<User> users = userRepository.findAll();
        System.out.println(users);
        Map<String, Object> response = new HashMap<>();
        response.put("body", users);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
    @GetMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        Map<String, Object> response = new HashMap<>();
        if (user.isPresent()) {
            User actualUser = user.get();
            System.out.println(actualUser);
            response.put("body", actualUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no user found");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
