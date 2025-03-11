package com.dashboard.v1.controller;

import com.dashboard.v1.entity.*;
import com.dashboard.v1.repository.ProjectRepository;
import com.dashboard.v1.repository.SurveyResponseRepository;
import com.dashboard.v1.repository.UserRepository;
import com.dashboard.v1.repository.VendorProjectLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class LinkRedirectController {

    private static final Logger logger = LoggerFactory.getLogger(LinkRedirectController.class);

    @Autowired
    private VendorProjectLinkRepository vendorProjectLinkRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/survey")
    public ResponseEntity<String> vendorClick(@RequestParam("uid") String uid,
                                              @RequestParam("pid") String pid,
                                              @RequestParam("token") String token,
                                              @RequestParam("country") String country,
                                              HttpServletRequest request) {

        logger.info("Received vendor click callback with uid: {} , pid: {}, token: {}", uid, pid, token);

        Optional<Project> projectOpt = projectRepository.findByProjectIdentifier(pid);
        if(!projectOpt.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Project not found for pid: " + pid);
        }
        if(projectOpt.get().getStatus() == ProjectStatus.CLOSED){
            return ResponseEntity.status(HttpStatus.GONE)
                    .body("Project is Closed : " + pid);
        }
        // Check if a survey response with the given uid already exists
        Optional<SurveyResponse> surveyResponseOpt = surveyResponseRepository.findByUId(uid);

        if (surveyResponseOpt.isPresent()) {
            // If the survey response already exists, it means the survey has been attempted.
            // Return a frontend page (or message) indicating that the survey has already been attempted.
            return ResponseEntity.ok("ALREADY ATTEMPTED SURVEY");
        } else {
            String ip = getClientIp(request);
            List<SurveyResponse> matchingIpSurveys = surveyResponseRepository.findByIpAddress(ip);
            for (SurveyResponse survey : matchingIpSurveys) {
                if (Objects.equals(survey.getProjectId(), pid)) {
                    return ResponseEntity.ok("ALREADY ATTEMPTED SURVEY");
                }
            }

            // If no survey response exists, process the click event.

            // Create a new SurveyResponse object.
            SurveyResponse newResponse = new SurveyResponse();
            newResponse.setUId(uid);
            newResponse.setProjectId(pid);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            newResponse.setTimeStart(now.toLocalDateTime());

            // Capture the user's IP address from the HttpServletRequest.
            newResponse.setIpAddress(ip);

            Optional<User> vendor = userRepository.findByToken(token);

            if (vendor.isPresent()) {
                newResponse.setVendorUsername(vendor.get().getUsername());
            } else {
                // Handle project not found scenario
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Vendor not found for token: " + token);
            }
            // Optionally, fetch project details using the pid.
            // For example, assume Project is an entity that contains details like the original link.
            String redirectUrl = null;
                Project project = projectOpt.get();
                // Example: set country from project details
                newResponse.setCountry(country);
                List<CountryLink> links = project.getCountryLinks();

                // Find the first matching country and get its originalLink
                redirectUrl = links.stream()
                        .filter(link -> Objects.equals(link.getCountry(), country))
                        .map(CountryLink::getOriginalLink)
                        .findFirst()
                        .orElse(null); // or set a default URL if needed


            // Set the survey status (for example, IN_PROGRESS)
            newResponse.setStatus(SurveyStatus.IN_PROGRESS);

            // Save the new survey response to the database.
            surveyResponseRepository.save(newResponse);

            // Build the headers to perform a redirect to the original link.
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(redirectUrl));

            // Return a redirect response (HTTP 302 Found) to send the user to the original frontend page.
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");  // Get IP from proxy/load balancer
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");  // Alternative header
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();  // Default method
        }

        // If multiple IPs (e.g., from proxies), get the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

}
