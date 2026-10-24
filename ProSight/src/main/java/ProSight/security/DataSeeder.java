package ProSight.security;

import ProSight.entity.User;
import ProSight.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        // Automatically provision an Admin account if missing
        if (userRepository.findByUsername("Admin_06").isEmpty()) {
            User admin = new User();
            admin.setUsername("Admin_06");
            admin.setPassword(passwordEncoder.encode("admin123")); // Default password
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("Seeded default Admin account.");
        }

        // Automatically provision a Standard Member account if missing
        if (userRepository.findByUsername("Member_01").isEmpty()) {
            User member = new User();
            member.setUsername("Member_01");
            member.setPassword(passwordEncoder.encode("member123")); // Default password
            member.setRole("ROLE_MEMBER");
            userRepository.save(member);
            System.out.println("Seeded default Member account.");
        }
    }
}