package ProSight.controller;

import ProSight.entity.User;
import ProSight.repository.UserRepository;
import ProSight.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken.");
        }

        // Cryptographically hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Ensure everyone defaults to a standard MEMBER role unless specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("MEMBER");
        }

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody User loginRequest) {
        try {
            // Verify the provided password against the BCrypt hash in the database
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        }

        // Fetch the verified user to get their role and ID
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

        // Generate the secure JWT Token
        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Package the token and user details to send back to React
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("id", user.getId());

        return ResponseEntity.ok(response);
    }

    // Open endpoint so the task assignee dropdowns still work
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}