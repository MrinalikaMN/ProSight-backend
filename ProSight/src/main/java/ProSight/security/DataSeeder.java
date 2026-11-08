package ProSight.security;

import ProSight.entity.User;
import ProSight.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Makes sure the two starter accounts exist.
 *
 * Passwords:
 *  - On your computer they default to admin123 / member123.
 *  - On Railway, set SEED_ADMIN_PASSWORD and SEED_MEMBER_PASSWORD. If those are set, the accounts'
 *    passwords are updated to match on every start, even if the accounts already existed,
 *    so the live site never keeps the public default passwords.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        ensureAccount("Admin_06", "ROLE_ADMIN", "SEED_ADMIN_PASSWORD", "admin123");
        ensureAccount("Member_01", "ROLE_MEMBER", "SEED_MEMBER_PASSWORD", "member123");
    }

    private void ensureAccount(String username, String role, String envName, String localDefault) {
        String fromEnv = System.getenv(envName);
        boolean envSet = fromEnv != null && !fromEnv.isBlank();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setRole(role);
            user.setPassword(passwordEncoder.encode(envSet ? fromEnv : localDefault));
            userRepository.save(user);
            System.out.println(">>> Created starter account " + username + (envSet ? " (password from " + envName + ")" : " (local default password)") + " <<<");
            return;
        }

        // Existing account: if the environment variable is set and differs, update the password.
        if (envSet && !passwordEncoder.matches(fromEnv, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(fromEnv));
            userRepository.save(user);
            System.out.println(">>> Updated " + username + " password from " + envName + " <<<");
        }
    }
}