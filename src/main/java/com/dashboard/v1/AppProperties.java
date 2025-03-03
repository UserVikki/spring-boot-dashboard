package com.dashboard.v1;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String secretKey;

    @PostConstruct
    public void init() {
        System.out.println("Secret Key Loaded: " + secretKey);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
