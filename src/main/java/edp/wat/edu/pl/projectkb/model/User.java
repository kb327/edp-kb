package edp.wat.edu.pl.projectkb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String role;

    public User(String username, String hashedPassword, String email, String name, String surname, LocalDateTime now, String role) {
        this.username = username;
        this.passwordHash = hashedPassword;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.createdAt = now;
        this.role = role;
    }

}
