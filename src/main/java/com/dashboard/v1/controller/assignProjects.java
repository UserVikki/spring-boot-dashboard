package com.dashboard.v1.controller;

import com.dashboard.v1.entity.*;
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

        List<User> vendorNotHidden = vendors.get().stream()
                .filter(v -> v.getIsShown() == IsRemoved.show)
                .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("vendors", vendorNotHidden);
        response.put("projects", availableForAssign);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign-projects")
    public ResponseEntity<String> assignProjectsToVendor(@RequestBody AssignProjectRequest request) {
        Optional<List<User>> vendorOpt = userRepository.findByIdIn(request.getVendorIds());
        if (!vendorOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vendor not found");
        }

        List<User> vendors = vendorOpt.get().stream().filter(vendor -> vendor.getIsShown() == IsRemoved.show)
                .collect(Collectors.toList());
        vendors.forEach(vendor ->{

                List<String> projects = vendor.getProjectsId();
                List<String> selectedProjects = request.getProjectIds();

                selectedProjects.forEach(projectId -> {
                    Optional<Project> optionalProject = projectRepository.findByProjectIdentifier(projectId);

                    if (optionalProject.isPresent() && !projects.contains(projectId) && optionalProject.get().getStatus() == ProjectStatus.OPEN) {
                        Project project = optionalProject.get();

                        List<String> vendorsOfProject = project.getVendorsUsername();
                        if (vendorsOfProject == null) {
                            vendorsOfProject = new ArrayList<>();
                        }

                        if (!vendorsOfProject.contains(vendor.getUsername())) {
                            vendorsOfProject.add(vendor.getUsername());
                            project.setVendorsUsername(vendorsOfProject);
                            projectRepository.save(project);
                            projects.add(projectId);
                        }


                    }
                });


                vendor.getProjectsId().addAll(projects);
                userRepository.save(vendor);

        });

        return ResponseEntity.ok("Projects assigned successfully!");
    }
}
