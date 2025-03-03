package com.dashboard.v1.model.request;

import lombok.Data;

@Data
public class ClientRequest {
    private String username;
    private String email;
    private String company;
}
