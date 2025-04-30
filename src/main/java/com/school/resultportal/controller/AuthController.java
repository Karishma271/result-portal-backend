package com.school.resultportal.controller;

import com.school.resultportal.model.Admin;
import com.school.resultportal.model.AuthRequest;
import com.school.resultportal.repository.AdminRepository;
import com.school.resultportal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest auth) {
        Admin admin = adminRepository.findByUsername(auth.getUsername());
        if (admin != null && admin.getPassword().equals(auth.getPassword())) {
            String token = jwtUtil.generateToken(admin.getUsername());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
