package com.dashboard.v1.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String projectIdentifier;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status; // ACTIVE or INACTIVE
    private String quota;
    private String loi;
    private String ir;
    private String counts;
    private String client;

    @ElementCollection
    @CollectionTable(name = "project_country_links", joinColumns = @JoinColumn(name = "project_id"))
    private List<CountryLink> countryLinks; // List of country-link pairs

    @ElementCollection
    @CollectionTable(name = "project_vendors_username", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "vendor_username")
    private List<String> vendorsUsername;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
