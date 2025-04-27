package com.dashboard.v1.entity;

import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class CountryLink {
    private CountryCode country;
    private String originalLink;
}
