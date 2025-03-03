package com.dashboard.v1.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class CountryLink {
    private String country;
    private String originalLink;
}
