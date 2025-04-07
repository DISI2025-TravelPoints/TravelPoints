package org.example.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "ID",
            updatable = false
    )
    private Long id;

    @Column(
            name ="Name",
            nullable = false
    )
    private String name;

    @Column(
            name="Email",
            nullable = false,
            unique = true
    )
    private String email;
    @Column(
            name="Password",
            nullable = false
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(
            name="Role",
            nullable = false
    )
    private UserRole role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
