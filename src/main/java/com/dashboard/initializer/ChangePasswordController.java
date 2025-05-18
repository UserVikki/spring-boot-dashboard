package com.dashboard.initializer;

import com.dashboard.v1.entity.User;
import com.dashboard.v1.model.request.ChangePasswordRequest;
import com.dashboard.v1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChangePasswordController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PutMapping("/change-password")
    public ResponseEntity<String> changeAdminPassword(@RequestBody ChangePasswordRequest request) {
        User admin = userRepository.findByUsername("admin")
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Current password is incorrect.");
        }

        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(admin);

        return ResponseEntity.ok("Admin password changed successfully.");
    }
}
