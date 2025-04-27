package com.dashboard.v1.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class IpToCountry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ipStart;     // Store as long (numeric IPv4)
    private Long ipEnd;
    private String countryCode; // e.g., "US", "IN"

    // getters and setters
}
