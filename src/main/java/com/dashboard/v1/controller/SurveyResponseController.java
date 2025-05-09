package com.dashboard.v1.controller;

import com.dashboard.v1.entity.*;
import com.dashboard.v1.repository.ProjectRepository;
import com.dashboard.v1.repository.SurveyResponseRepository;
import com.dashboard.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/survey")
@RequiredArgsConstructor
public class SurveyResponseController {

    private static final Logger logger = LoggerFactory.getLogger(SurveyResponseController.class);

    private final SurveyResponseRepository surveyResponseRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @GetMapping("/complete")
    public ResponseEntity<?> submitComplete(@RequestParam String PID, @RequestParam String UID) {
        logger.info("inside SurveyResponseController /survey/complete UID : {}, PID : {}", UID, PID);
        return saveSurveyResponse(UID, SurveyStatus.COMPLETE);
    }

    @GetMapping("/terminate")
    public ResponseEntity<?> submitTerminate(@RequestParam String PID, @RequestParam String UID) {
        logger.info("inside SurveyResponseController /survey/terminate UID : {}, PID : {}", UID, PID);
        return saveSurveyResponse(UID, SurveyStatus.TERMINATE);
    }

    @GetMapping("/quotafull")
    public ResponseEntity<?> submitQuotaFull(@RequestParam String PID, @RequestParam String UID) {
        logger.info("inside SurveyResponseController /survey/quotafull UID : {}, PID : {}", UID, PID);
        return saveSurveyResponse(UID, SurveyStatus.QUOTAFULL);
    }

    private ResponseEntity<?> saveSurveyResponse(String UID, SurveyStatus status) {
        // Validate the project exists.


        Optional<SurveyResponse> surveyResponse = surveyResponseRepository.findByUId(UID);

        if(!surveyResponse.isPresent()){
            return ResponseEntity.ok("Survey response does not match with any vendor click");
        }

        Project project = projectRepository.findByProjectIdentifier(surveyResponse.get().getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
        // Retrieve the SurveyResponse object from the Optional
        SurveyResponse res = surveyResponse.get();
        if(!(surveyResponse.get().getStatus() == SurveyStatus.IN_PROGRESS)) return ResponseEntity.ok("THIS UID is already registered for a response ");;
        // Set the provided survey status (e.g., COMPLETE, TERMINATE, QUOTAFULL) to the survey response
        res.setStatus(status);

        surveyResponseRepository.save(res);

        if(status == SurveyStatus.COMPLETE){
            int currentCount = project.getCounts() != null ? Integer.parseInt(project.getCounts()) : 0;
            project.setCounts(String.valueOf(currentCount + 1));
            projectRepository.save(project);
        }
        Optional<User> vendor = userRepository.findByUsername(res.getVendorUsername());

        if(!vendor.isPresent())
        {
            logger.info("vendor not found for this survey submission projectId : {}, UID : {} ", project.getProjectIdentifier(), UID);
            return ResponseEntity.ok("vendor not found for this survey submission");
        }
        // get the vendor api according to status (complete,terminate,quotafull) and make a http request to them with PID and UID
        String vendorApiUrl = getVendorApiUrl(vendor.get(), status);

        if (vendorApiUrl == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid survey status");
        }

        // Prepare request parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(vendorApiUrl)
                .queryParam("pid", project.getProjectIdentifier())
                .queryParam("uid", UID);

        try {
            // Make HTTP GET request to the vendor API
            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
            return ResponseEntity.ok("Vendor notified successfully: " + response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to notify vendor: " + e.getMessage());
        }
    }

    @GetMapping("/api/survey-responses/all")
    public List<SurveyResponse> getAllSurveyResponses() {
        return surveyResponseRepository.findAll();
    }

    private String getVendorApiUrl(User vendor, SurveyStatus status) {
        switch (status) {
            case COMPLETE:
                return vendor.getComplete();
            case TERMINATE:
                return vendor.getTerminate();
            case QUOTAFULL:
                return vendor.getQuotafull();
            default:
                return null;
        }
    }
}
