package com.dashboard.v1.service;

import com.neovisionaries.i18n.CountryCode;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {
    public List<CountryDTO> getAllCountries() {
        return Arrays.stream(CountryCode.values())
                .filter(code -> code.getName() != null)
                .map(code -> new CountryDTO(code.name(), code.getName()))
                .collect(Collectors.toList());
    }
    @Getter
    public static class CountryDTO {
        // Getters and setters
        private String code;
        private String name;
        public CountryDTO(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}