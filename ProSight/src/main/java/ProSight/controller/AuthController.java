package ProSight.controller;

import ProSight.entity.User;
import ProSight.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. The Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return ResponseEntity.ok(Map.of("message", "Login successful", "username", username));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }

    // 2. Setup Endpoint to create your first user
    @GetMapping("/setup")
    public ResponseEntity<?> setupAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return ResponseEntity.badRequest().body("Admin already exists.");
        }

        User admin = new User("admin", "securepassword123", "ADMIN");
        userRepository.save(admin);
        return ResponseEntity.ok("Admin user created successfully. Username: admin | Password: securepassword123");
    }
}