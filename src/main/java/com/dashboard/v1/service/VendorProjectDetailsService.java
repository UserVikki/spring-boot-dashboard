package com.dashboard.v1.service;

import com.dashboard.v1.entity.SurveyResponse;
import com.dashboard.v1.entity.SurveyStatus;
import com.dashboard.v1.model.response.VendorProjectDetailsResponse;
import com.dashboard.v1.repository.SurveyResponseRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VendorProjectDetailsService {

    private SurveyResponseRepository surveyResponseRepository;

    public List<VendorProjectDetailsResponse> getProjectsDetails(String username) {
        Optional<List<SurveyResponse>> surveysOptional = surveyResponseRepository.findByVendorUsername(username);

        if (!surveysOptional.isPresent()) return new ArrayList<>();

        List<SurveyResponse> surveys = surveysOptional.get();

        // Group by pId and count occurrences of each status
        Map<String, VendorProjectDetailsResponse> responseMap = new HashMap<>();

        for (SurveyResponse survey : surveys) {
            String projectId = survey.getProjectId();
            SurveyStatus status = survey.getStatus();

            // Get or create the response object
            VendorProjectDetailsResponse response = responseMap.getOrDefault(projectId, new VendorProjectDetailsResponse());
            response.setPid(projectId);

            // Count based on status
            switch (status) {
                case COMPLETE:
                    response.setComplete(String.valueOf(Integer.parseInt(response.getComplete() == null ? "0" : response.getComplete()) + 1));
                    break;
                case TERMINATE:
                    response.setTerminate(String.valueOf(Integer.parseInt(response.getTerminate() == null ? "0" : response.getTerminate()) + 1));
                    break;
                case QUOTAFULL:
                    response.setQuotafull(String.valueOf(Integer.parseInt(response.getQuotafull() == null ? "0" : response.getQuotafull()) + 1));
                    break;
            }

            responseMap.put(projectId, response);
        }

        return new ArrayList<>(responseMap.values());
    }

}
