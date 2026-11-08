package ProSight.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    // Readable from login/register requests, but never included in responses
    // (the /api/auth/users list used to expose every password hash).
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    // Where deadline emails go. Accepted from register/settings requests but never listed publicly.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String email;

    // Each person can switch deadline emails off.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean notifyByEmail = true;

    // NEW: Officially tracking roles in the database
    @Column(nullable = false)
    private String role = "MEMBER";

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isNotifyByEmail() { return notifyByEmail == null || notifyByEmail; }
    public void setNotifyByEmail(Boolean notifyByEmail) { this.notifyByEmail = notifyByEmail; }

    /** Shown in the team list so admins can see who will get emails, without revealing the address. */
    @JsonProperty(value = "hasEmail", access = JsonProperty.Access.READ_ONLY)
    public boolean hasEmail() { return email != null && !email.isBlank(); }
}