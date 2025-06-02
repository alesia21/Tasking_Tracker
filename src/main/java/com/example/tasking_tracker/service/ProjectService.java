package com.example.tasking_tracker.service;
import com.example.tasking_tracker.entity.Project;
import com.example.tasking_tracker.entity.User;
import com.example.tasking_tracker.exception.ResourceNotFoundException;
import com.example.tasking_tracker.repository.ProjectRepository;
import com.example.tasking_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;


    public Project createProject(Project project) {
        Long ownerId = project.getOwner().getId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        project.setOwner(owner);
        return projectRepository.save(project);
    }


    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
    }


    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }


    public Project updateProject(Long projectId, Project projectRequest) {
        Project existing = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        // Only update the mutable fields:
        existing.setName(projectRequest.getName());
        existing.setDescription(projectRequest.getDescription());
        return projectRepository.save(existing);
    }


    public void deleteProject(Long projectId) {
        Project existing = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));
        projectRepository.delete(existing);
    }
}
