package com.school.resultportal.controller;

import com.school.resultportal.model.Admin;
import com.school.resultportal.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Admin admin = adminRepository.findByUsername(username);
        if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(403).body("Invalid credentials");
    }

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
