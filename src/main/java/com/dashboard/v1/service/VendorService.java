
package com.dashboard.v1.service;

import com.dashboard.v1.entity.User;
import com.dashboard.v1.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendorService {

//    @Value("${app.domain}") // Load your domain from application.properties
    private String domain = "http://localhost:8080";

    private final UserRepository userRepository;

    public VendorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllVendors() {
        return userRepository.findAllVendors();
    }

    public String generateVendorUrl(String vendorId) {
        Optional<User> vendor = userRepository.findByUsername(vendorId);
        if (!vendor.isPresent()) {
            throw new RuntimeException("Vendor not found");
        }
        return domain + "/survey/" + vendor.get().getUserToken();
    }
}
