package com.dashboard.v1.entity;

import javax.persistence.Entity;  // ✅ Use Java EE for Java 8
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class VendorProjectLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A unique token which will be part of the generated URL.
    private String generatedToken;

    private String country;
    private String originalLink;

    private String projectId;

    private String vendorId;
}
