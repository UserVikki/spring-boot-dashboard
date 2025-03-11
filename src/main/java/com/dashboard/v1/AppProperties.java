package com.dashboard.v1;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String secretKey;

    private String domain;

    @PostConstruct
    public void init() {
//        System.out.println("Secret Key Loaded: " + secretKey);
//        System.out.println("Domain Loaded: " + domain);
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
