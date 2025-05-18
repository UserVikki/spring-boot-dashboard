package com.dashboard.v1.model.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;

    // Getters and Setters
}
