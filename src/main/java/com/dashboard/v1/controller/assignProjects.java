package com.dashboard.v1.controller;

import com.dashboard.v1.entity.Project;
import com.dashboard.v1.entity.ProjectStatus;
import com.dashboard.v1.entity.Role;
import com.dashboard.v1.entity.User;
import com.dashboard.v1.model.request.AssignProjectRequest;
import com.dashboard.v1.repository.ProjectRepository;
import com.dashboard.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class assignProjects {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    // Get all vendors and projects
    @GetMapping("/vendors-projects")
    public ResponseEntity<Map<String, Object>> getVendorsAndProjects() {
        Optional<List<User>> vendors = userRepository.findByRole(Role.VENDOR);
        List<Project> projects = projectRepository.findAll();

        List<Project> availableForAssign = projects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.OPEN)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("vendors", vendors);
        response.put("projects", availableForAssign);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-projects")
    public ResponseEntity<String> assignProjectsToVendor(@RequestBody AssignProjectRequest request) {
        Optional<User> vendorOpt = userRepository.findById(request.getVendorId());
        if (!vendorOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }

        User vendor = vendorOpt.get();
        List<String> selectedProjects = request.getProjectIds();

        selectedProjects.forEach(projectId -> {
            Optional<Project> optionalProject = projectRepository.findByProjectIdentifier(projectId);

            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();

                List<String> vendors = project.getVendorsUsername();
                if (vendors == null) {
                    vendors = new ArrayList<>();
                }

                vendors.add(vendor.getUsername());
                project.setVendorsUsername(vendors);
                projectRepository.save(project);
            }
        });


        vendor.getProjectsId().addAll(selectedProjects);
        userRepository.save(vendor);

        return ResponseEntity.ok("Projects assigned successfully!");
    }
}
