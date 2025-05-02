@PostMapping("/login")
public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
    String username = credentials.get("username");
    String password = credentials.get("password");

    Admin admin = adminRepository.findByUsername(username);
    if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
        return ResponseEntity.ok("Login successful. You are now logged in as admin.");
    }
    return ResponseEntity.status(401).body("Invalid credentials");
}
