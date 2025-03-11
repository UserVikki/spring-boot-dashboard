package com.dashboard.v1.controller;

import com.dashboard.v1.entity.Client;
import com.dashboard.v1.entity.Project;
import com.dashboard.v1.model.request.CreateProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RedirectsLinkController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectsLinkController.class);

    @GetMapping("/redirects")
    public ResponseEntity<?> getRedirects() {
        logger.info("inside RedirectsController /redirects");
        Map<String, String> response = new HashMap<>();

        response.put("complete", "http://82.25.104.149:8080/survey/complete?UID=111&PID=p0");
        response.put("terminate", "http://82.25.104.149:8080/survey/terminate?UID=111&PID=p0");
        response.put("quotafull", "http://82.25.104.149:8080/survey/quotafull?UID=111&PID=p0");

        return ResponseEntity.ok(response);
    }
}
