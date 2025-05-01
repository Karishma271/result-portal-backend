package com.school.resultportal.controller;

import com.school.resultportal.model.Admin;
import com.school.resultportal.repository.AdminRepository;
import com.school.resultportal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jwtUtil.generateToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // ✅ Temporary admin reset API (no auth required)
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
