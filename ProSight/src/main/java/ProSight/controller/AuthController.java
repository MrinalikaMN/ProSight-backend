package ProSight.controller;

import ProSight.entity.User;
import ProSight.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // In a production app, we would use BCrypt for password hashing here
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        // Default new signups to 'MEMBER' if no role is specified
        if (newUser.getRole() == null || newUser.getRole().isEmpty()) {
            newUser.setRole("MEMBER");
        }

        User savedUser = userRepository.save(newUser);
        return ResponseEntity.ok(savedUser);
    }

    // Endpoint to fetch all team members for your Team page
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}