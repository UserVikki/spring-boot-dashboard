package com.dashboard.v1.model.request;

import lombok.Data;

import java.util.List;

@Data
public class AssignProjectRequest {
    private List<Long> vendorIds;
    private List<String> projectIds;
}