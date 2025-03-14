package com.dashboard.v1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

        response.put("complete", "https://rfhivex.com/survey/complete?UID=111&PID=p0");
        response.put("terminate", "https://rfhivex.com/survey/terminate?UID=111&PID=p0");
        response.put("quotafull", "https://rfhivex.com/survey/quotafull?UID=111&PID=p0");

        return ResponseEntity.ok(response);
    }
}
