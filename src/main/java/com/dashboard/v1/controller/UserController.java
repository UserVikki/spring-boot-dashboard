package com.dashboard.v1.controller;

import com.dashboard.v1.entity.Client;
import com.dashboard.v1.entity.IsRemoved;
import com.dashboard.v1.entity.User;
import com.dashboard.v1.repository.ClientRepository;
import com.dashboard.v1.repository.ProjectRepository;
import com.dashboard.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @GetMapping("/status/update/{username}")
    @Transactional
    public ResponseEntity<?> getAndUpdateUserStatus(@PathVariable String username) {
        logger.info("inside ClientController /status/update/{username} username : {} ", username);

        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Toggle status
            IsRemoved newStatus = (user.getIsShown() == IsRemoved.show) ? IsRemoved.hide : IsRemoved.show;
            user.setIsShown(newStatus);
            userRepository.save(user);

            return ResponseEntity.ok(Collections.singletonMap("success", newStatus));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }

    }

    @GetMapping("/client/status/update/{username}")
    @Transactional
    public ResponseEntity<?> getAndUpdateClientStatus(@PathVariable String username) {
        logger.info("inside ClientController /status/client/update/{username} username : {} ", username);

        Optional<Client> optionalUser = clientRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            Client client = optionalUser.get();

            // Toggle status
            IsRemoved newStatus = (client.getIsShown() == IsRemoved.show) ? IsRemoved.hide : IsRemoved.show;
            client.setIsShown(newStatus);
            clientRepository.save(client);

            return ResponseEntity.ok(Collections.singletonMap("success", newStatus));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Client not found"));
        }

    }
}
