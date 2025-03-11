package com.dashboard.v1.controller;

import com.dashboard.v1.entity.Client;
import com.dashboard.v1.entity.Project;
import com.dashboard.v1.entity.ProjectStatus;
import com.dashboard.v1.entity.SurveyResponse;
import com.dashboard.v1.model.request.CreateProjectRequest;
import com.dashboard.v1.model.response.VendorProjectDetailsResponse;
import com.dashboard.v1.repository.ClientRepository;
import com.dashboard.v1.repository.ProjectRepository;
import com.dashboard.v1.service.VendorProjectDetailsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository projectRepository;

    private final VendorProjectDetailsService vendorProjectDetailsService;

    private final ClientRepository clientRepository;

    @PostMapping("/create/project")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProject(@RequestBody CreateProjectRequest request) {
        logger.info("inside ProjectController /create/project ");
        Map<String, Object> response = new HashMap<>();
         try{
            Project project = new Project();
            project.setProjectIdentifier(request.getProjectIdentifier());
            project.setStatus(request.getStatus());
            project.setQuota(request.getQuota());
            project.setIr(request.getIr());
            project.setCounts(request.getCounts());
            project.setCountryLinks(request.getCountryLinks());
            project.setLoi(request.getLoi());
            project.setClient(request.getClientUsername());
            Optional<Client> client =  clientRepository.findByUsername(request.getClientUsername());
            if(!client.isPresent()) {
                response.put("success", false);
                response.put("message", "Something went wrong! " + "Client does not exist");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            List<String> projects = client.get().getProjects();
            projects.add(request.getProjectIdentifier());
            client.get().setProjects(projects);
            clientRepository.save(client.get());
            projectRepository.save(project);
            response.put("success", true);
            response.put("message", "Project created successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Something went wrong! " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Project> getAllProjects() {
        logger.info("inside ProjectController /projects/all ");
        return projectRepository.findAll();
    }

    // ✅ Fetch projects assigned to vendor (Vendor only)
    @GetMapping("/vendor")
    public List<VendorProjectDetailsResponse> getVendorProjects(@RequestParam String username) {
        logger.info("inside ProjectController /projects/vendor ");
        return vendorProjectDetailsService.getProjectsDetails(username);
    }

    // ✅ Filter and paginate projects for Admin
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Project> filterAdminProjects(
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String quota,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String counts,
            @RequestParam(required = false) String ir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("inside ProjectController /projects/filter ");
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.filterProjects(projectId, quota, status, counts, ir, pageable);
    }

    // ✅ Filter and paginate vendor's assigned projects
    @GetMapping("/filter/vendor")
    @PreAuthorize("hasRole('VENDOR')")
    public Page<Project> filterVendorProjects(
            @RequestParam String username, // Vendor username required
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String quota,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String counts,
            @RequestParam(required = false) String ir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("inside ProjectController /projects/filter/vendor ");
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.filterVendorProjects(username, projectId, quota, status, counts, ir, pageable);
    }

    // ✅ Fetch full project details (Admin can view any project)
    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Project> getProjectDetails(@PathVariable Long projectId) {
        logger.info("inside ProjectController /projects/{projectId} projectId : {} ", projectId);
        return projectRepository.findById(projectId);
    }

    // ✅ Fetch current project status by projectId and update it
    @GetMapping("/status/update/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void getAndUpdateProjectStatus(@PathVariable String projectId) {
        logger.info("inside ProjectController /status/get-update/{projectId} projectId : {} ", projectId);

        try {
            Project oldProject = projectRepository.findByProjectIdentifier(projectId).get();
            ProjectStatus currentStatus = oldProject.getStatus();

            if (currentStatus.equals(ProjectStatus.OPEN)) {
                projectRepository.updateProjectStatusByProjectId(ProjectStatus.CLOSED, projectId);
            } else {
                projectRepository.updateProjectStatusByProjectId(ProjectStatus.OPEN, projectId);
            }

        } catch (Exception e) {
            logger.error("Error while fetch project by projectId : {}", projectId);
        }
    }

}
