package com.dashboard.v1;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {

    private String secretKey;

    private String domain;

    private String tokenForIPInfo;

    private String companyName;

    @PostConstruct
    public void init() {
//        System.out.println("Secret Key Loaded: " + secretKey);
//        System.out.println("Domain Loaded: " + domain);
    }
}
