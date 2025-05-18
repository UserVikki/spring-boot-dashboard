package com.dashboard.v1.controller;

import com.dashboard.v1.entity.*;
import com.dashboard.v1.repository.ProjectRepository;
import com.dashboard.v1.repository.SurveyResponseRepository;
import com.dashboard.v1.repository.UserRepository;
import com.dashboard.v1.repository.VendorProjectLinkRepository;
import com.dashboard.v1.service.IPInfoService;
import com.dashboard.v1.util.SslUtil;
import com.dashboard.v1.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private IPInfoService iPInfoService;

    @Autowired
    private UrlUtils urlUtils;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/survey")
    public ResponseEntity<String> vendorClick(@RequestParam("uid") String uid,
                                              @RequestParam("pid") String pid,
                                              @RequestParam("token") String token,
                                              @RequestParam("country") String country,
                                              HttpServletRequest request) throws UnsupportedEncodingException {

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

            SslUtil.disableSslVerification();

            String countryCode = iPInfoService.getIPInfo(ip);

            if(countryCode!=null && !countryCode.equalsIgnoreCase(country)) return ResponseEntity.ok("Access restricted: Your IP address does not correspond to the selected country. ;)");

            List<SurveyResponse> matchingIpSurveys = surveyResponseRepository.findByIpAddress(ip, pid);
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
                    .filter(link -> Objects.equals(link.getCountry().name(), country))
                    .map(CountryLink::getOriginalLink)
                    .findFirst()
                    .orElse(null); // or set a default URL if needed

            if(redirectUrl == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("no survey links found in the project, please create new project with same links properly");
            }

            String url = redirectUrl.trim();


            // Set the survey status (for example, IN_PROGRESS)
            newResponse.setStatus(SurveyStatus.IN_PROGRESS);

            // Save the new survey response to the database.
            surveyResponseRepository.save(newResponse);
            // Build the headers to perform a redirect to the original link.
            HttpHeaders headers = new HttpHeaders();
            // Regex pattern to find the `uid=` parameter and replace its value
//            Pattern pattern = Pattern.compile("([?&]uid=)([^&]*)");
//            Matcher matcher = pattern.matcher(originalLink);

//            String key = "uid"; // this can be any key
//
//            // Build regex dynamically to find 'key=...'
//            String regex = key + "=[^&]*$";
//
//            if (url.matches(".*" + regex)) {
//                // Replace existing value for the key at the end
//                url = url.replaceAll(regex, key + "=" + uid);
//            } else {
//                // Append new key-value if not present
//                url += (url.contains("?") ? "&" : "?") + key + "=" + uid;
//            }

// Step 1: Extract last key from the URL

            url = urlUtils.updateIfConditionMatches(url,uid);


//            String key = null;
//            int queryStart = url.indexOf('?');
//            if (queryStart != -1) {
//                String query = url.substring(queryStart + 1);
//                String[] params = query.split("&");
//                if (params.length > 0) {
//                    String lastParam = params[params.length - 1];
//                    int equalIndex = lastParam.indexOf('=');
//                    if (equalIndex != -1) {
//                        key = lastParam.substring(0, equalIndex);
//                    }
//                }
//            }
//
//            if (key != null) {
//
//                // Step 2: Build regex and matcher
//                Pattern pattern = Pattern.compile("([?&]" + key + "=)([^&]*)");
//                Matcher matcher = pattern.matcher(url);
//
//                if (matcher.find()) {
//                    // Replace existing value of the last key
//                    url = matcher.replaceFirst(matcher.group(1) + uid);
//                } else {
//                    // Append if key not found
//                    url += (url.contains("?") ? "&" : "?") + key + "=" + uid;
//                }
//            }

            System.out.println(url);



            if (!isValidURL(url)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid redirect URL: " + url);
            }
            headers.setLocation(URI.create(url));

            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(url)).build();
        }
    }

    private boolean isValidURL(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
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
