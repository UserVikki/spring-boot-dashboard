package com.dashboard.v1.service;

import com.dashboard.v1.AppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class IPInfoService {

    private static final Logger log = LoggerFactory.getLogger(IPInfoService.class);


    public final AppProperties appProperties;
    public String getIPInfo(String ipAddress) {
        String apiUrl = "https://ipinfo.io/" + ipAddress + "?token=" + appProperties.getTokenForIPInfo();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                // Token expired or invalid request
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());

            // Check if 'country' field exists
            if (root.has("country")) {
                return root.get("country").asText(); // Return the value of country
            } else {
                return null; // country not present
            }

        } catch (Exception e) {
            log.error("Error while fetching IP info", e);
            return null;
        }
    }
}
