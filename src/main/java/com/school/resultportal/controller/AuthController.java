package com.school.resultportal.controller;

import com.school.resultportal.model.Admin;
import com.school.resultportal.model.AuthRequest;
import com.school.resultportal.repository.AdminRepository;
import com.school.resultportal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Login API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Admin admin = adminRepository.findByUsername(request.getUsername());
            if (admin == null) {
                return ResponseEntity.status(401).body("Invalid username or password.");
            }

            String token = jwtUtil.generateToken(admin.getUsername());
            return ResponseEntity.ok(Collections.singletonMap("token", token));

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }
    }

    // ✅ Temporary admin reset API
    @PostMapping("/admin/reset")
    public ResponseEntity<String> resetAdmin() {
        Admin admin = adminRepository.findByUsername("admin");
        if (admin == null) {
            admin = new Admin();
        }
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        adminRepository.save(admin);
        return ResponseEntity.ok("✅ Admin reset to admin / admin123");
    }
}
