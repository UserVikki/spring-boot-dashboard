package com.dashboard.v1.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "client")
@Data
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;  // Store hashed password

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String token;

    @ElementCollection
    @CollectionTable(name = "client_projects", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "project_id")
    private List<String> projects;  // Stores project IDs as a list

    @PrePersist
    public void generateToken() {
        if (token == null || token.isEmpty()) {
            this.token = UUID.randomUUID().toString(); // Generates a unique token
        }
    }
}
