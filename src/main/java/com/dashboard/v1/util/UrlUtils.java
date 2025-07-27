package com.dashboard.v1.util;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component
public class UrlUtils {

    public Map<String, String> getQueryParams(String url) {
        Map<String, String> queryPairs = new HashMap<>();
        try {
            String[] parts = url.split("\\?");
            if (parts.length > 1) {
                String query = parts[1];
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                    String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
                    queryPairs.put(key, value);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return queryPairs;
    }

    public String rebuildUrl(String originalUrl, Map<String, String> updatedParams) {
        try {
            String baseUrl = originalUrl.split("\\?")[0];
            StringBuilder sb = new StringBuilder(baseUrl);
            if (!updatedParams.isEmpty()) {
                sb.append("?");
                for (Map.Entry<String, String> entry : updatedParams.entrySet()) {
                    sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sb.append("=");
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    sb.append("&");
                }
                sb.deleteCharAt(sb.length() - 1); // Remove trailing '&'
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return originalUrl;
        }
    }

    public String updateIfConditionMatches(String url, String newValue) {

        String temp = url.replace("[UID]", newValue);
        if(!temp.equals(url)) return temp;

        Map<String, String> paramMap = getQueryParams(url);

        // 1. Look for value that starts and ends with brackets
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            String value = entry.getValue();
            if (value != null && value.startsWith("[") && value.endsWith("]")) {
                paramMap.put(entry.getKey(), newValue);
                return rebuildUrl(url, paramMap);
            }
        }

        for (String key : paramMap.keySet()) {
            if (key.equalsIgnoreCase("UID")) {
                paramMap.put(key, newValue);
                return rebuildUrl(url, paramMap);
            }
        }

        for (String key : paramMap.keySet()) {
            if (key.equalsIgnoreCase("PID")) {
                paramMap.put(key, newValue);
                return rebuildUrl(url, paramMap);
            }
        }

        // 4. Look for any empty value
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            String value = entry.getValue();
            if (value == null || value.trim().isEmpty()) {
                paramMap.put(entry.getKey(), newValue);
                return rebuildUrl(url, paramMap);
            }
        }

        // No condition matched
        return url;
    }

}
