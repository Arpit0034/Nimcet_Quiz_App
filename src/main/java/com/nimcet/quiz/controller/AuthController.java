package com.nimcet.quiz.controller;

import com.nimcet.quiz.config.JwtConfig;
import com.nimcet.quiz.model.User;
import com.nimcet.quiz.repository.UserRepository;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String email = req.get("email");
        String password = req.get("password");

        // ✅ Check before saving
        if (userRepo.existsByUsername(username))
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Username already exists"));

        if (userRepo.existsByEmail(email))
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered! Please login."));

        try {
            User user = User.builder()
                    .username(username).email(email)
                    .password(encoder.encode(password)).role("STUDENT")
                    .build();
            userRepo.save(user);
            String token = jwtConfig.generateToken(username, "STUDENT");
            return ResponseEntity.ok(Map.of(
                    "token", token, "username", username, "role", "STUDENT","userId", user.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

        User user = userRepo.findByUsername(username)
                .orElse(null);

        if (user == null || !encoder.matches(password, user.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message","Invalid credentials"));

        String token = jwtConfig.generateToken(username, user.getRole());
        return ResponseEntity.ok(Map.of(
                "token", token, "username", username, "role", user.getRole(), "userId", user.getId()
        ));
    }
}