package com.dashboard.v1.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendorResponse {
    private String username;
    private String generatedPassword;
}
